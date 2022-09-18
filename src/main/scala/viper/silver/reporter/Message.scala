// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.
//
// Copyright (c) 2011-2019 ETH Zurich.

package viper.silver.reporter

import viper.silver.verifier._

/**
  * The only possible messages for the reporter are defined in this file.
  *
  * TODO: in case this file is modified, please remember to add/edit the Json
  * TODO:               converter(s) in `viper/server/ViperIDEProtocol.scala`.
  *
  */
sealed trait Message {
  override def toString: String = s"generic_message"
  val name: String
}

/**
 * Since AST construction is conceptually independent from verification,
 * e.g. in ViperCoreServer, it might be convenient to use a separate sub-hierarcy of messages
 * for purposes related to AST generation. Currently, messages such as [[WarningsDuringParsing]]
 * are used.
 *
 * These messages already have their JSON marshallers defined in
 * viper/server/frontends/http/jsonWriters/ViperIDEProtocol.scala
 *
 * ATG 2020
 */
sealed trait AstConstructionResultMessage extends Message {
  override val name: String = s"ast_construction_result"
  def astConstructionTime: Time
}

case class AstConstructionSuccessMessage(astConstructionTime: Time)
  extends AstConstructionResultMessage {
  override def toString: String =
    s"ast_construction_success(time=${astConstructionTime.toString})"
}

case class AstConstructionFailureMessage(astConstructionTime: Time, result: Failure)
  extends AstConstructionResultMessage {
  override def toString: String =
    s"ast_construction_failure(" +
    s"time=${astConstructionTime.toString}, " +
    s"result=${result.toString})"
}

sealed trait VerificationResultMessage extends Message {
  override val name: String = s"verification_result"
  def result: VerificationResult
  val verifier: String
}

object VerificationResultMessage {
  /** Create a [[VerificationResultMessage]] concerning verification of a full program, depending on the type of the
    * provided `result`:
    *  if `result` is [[Success]] then an [[OverallSuccessMessage]] is created, otherwise (if `result` is
    *  a [[Failure]]) a [[OverallFailureMessage]] is created.
    */
  def apply(verifier: String, verificationTime: Time, result: VerificationResult)
  : VerificationResultMessage = {

    result match {
      case Success => 
        OverallSuccessMessage(verifier, verificationTime)
      case failure: Failure => 
        OverallFailureMessage(verifier, verificationTime, failure)
    }
  }

  /** Create a [[VerificationResultMessage]] concerning a particular program [[Entity]], depending on the type of the
    * provided `result`:
    *  if `result` is [[Success]] then an [[EntitySuccessMessage]] is created, otherwise (if `result`
    *  is a [[Failure]]) a [[EntityFailureMessage]] is created.
    */
  def apply(verifier: String, entity: Entity, verificationTime: Time,
            result: VerificationResult)
  : VerificationResultMessage = {

    result match {
      case Success => 
        EntitySuccessMessage(verifier, entity, verificationTime)
      case failure: Failure => 
        EntityFailureMessage(verifier, entity, verificationTime, failure)
    }
  }

  def apply(verifier: String, entity: Entity, verificationTime: Time, 
            result: VerificationResult, cached: Boolean)
  : VerificationResultMessage = {

    result match {
      case Success => 
        EntitySuccessMessage(verifier, entity, verificationTime, cached)
      case failure: Failure => 
        EntityFailureMessage(verifier, entity, verificationTime, failure, cached)
    }
  }
}

trait CachedEntityMessage extends VerificationResultMessage

object CachedEntityMessage {

  def apply(verifier: String, entity: Entity, result: VerificationResult)
  : VerificationResultMessage =
    result match {
      case Success => 
        EntitySuccessMessage(verifier, entity, 0, cached = true)
      case failure: Failure =>
        EntityFailureMessage(verifier, entity, 0, failure, cached = true)
    }
}

// Overall results concern results for the entire program (e.g. those presently 
// produced by the Carbon backend)
case class OverallSuccessMessage(verifier: String, verificationTime: Time)
  extends VerificationResultMessage {

  override def toString: String = s"overall_success_message(" +
    s"verifier=${verifier}, time=${verificationTime.toString})"

  val result: VerificationResult = Success
}

case class OverallFailureMessage(verifier: String, verificationTime: Time, result: Failure)
  extends VerificationResultMessage {

  override def toString: String = s"overall_failure_message(" +
    s"verifier=${verifier}, time=${verificationTime.toString}, result=${result.toString})"
}

// Entity results concern results for specific program entities (these are presently 
// produced by the Silicon backend)
case class EntitySuccessMessage(verifier: String, concerning: Entity, 
                                verificationTime: Time, cached: Boolean = false)
  extends VerificationResultMessage {

  override def toString: String = s"entity_success_message(" +
    s"verifier=$verifier, " +
    s"concerning=${print(concerning)}, time=${verificationTime.toString}, cached=$cached)"

 val result: VerificationResult = Success
}

case class EntityFailureMessage(verifier: String, concerning: Entity, 
                                verificationTime: Time, result: Failure, cached: Boolean = false)
  extends VerificationResultMessage {

  override def toString: String = s"entity_failure_message(" +
      s"verifier=$verifier, concerning=${print(concerning)}, " +
      s"time=${verificationTime.toString}, result=${result.toString}, cached=$cached)"
}

case class StatisticsReport(nOfMethods: Int, nOfFunctions: Int, nOfPredicates: Int, 
                            nOfDomains: Int, nOfFields: Int)
  extends Message {

  override def toString: String = s"statistics_report(" +
    s"nom=${nOfMethods.toString}, nofu=${nOfFunctions.toString}, nop=${nOfPredicates.toString}, " +
    s"nod=${nOfDomains.toString}, nofi=${nOfFields.toString})"

  override val name = s"statistics"
}

case class ProgramOutlineReport(members: List[Entity]) extends Message {

  override def toString: String = s"program_outline_report(members=${members.map(print)})"
  override val name: String = s"program_outline"
}

case class ProgramDefinitionsReport(definitions: List[Definition]) extends Message {

  override def toString: String = s"program_definitions_report(definitions=${definitions.toString}"
  override val name: String = s"program_definitions"
}

// TODO: Variable level of detail?
case class ExecutionTraceReport(memberTraces: Seq[Any],
                                axioms: List[Any],
                                functionPostAxioms: List[Any]
                               ) extends Message {

  override def toString: String =
    s"""symbolic_execution_logger_report(
       |  members=${(memberTraces map {m => m.toString}).mkString("[", ",", "]")},
       |  axioms=${axioms.toString}
       |  functionPostAxioms=${functionPostAxioms.toString}
       |)""".stripMargin

  override val name: String = s"symbolic_execution_logger_report"
}

case class ExceptionReport(e: java.lang.Throwable) extends Message {

  override def toString: String = s"exception_report(e=${e.toString})"
  override val name: String = s"exception_report"
}

case class InvalidArgumentsReport(tool_signature: String, errors: List[AbstractError])
  extends Message {

  override def toString: String =
    s"invalid_args_report(tool_signature=${tool_signature}, errors=[${errors.mkString(",")}])"
  override val name: String = s"invalid_args_report"
}

case class ExternalDependenciesReport(deps: Seq[Dependency]) extends Message {

  override def toString: String = s"external_dependencies_report(deps=[${deps.mkString(",")}])"
  override val name: String = s"external_dependencies_report"
}

case class WarningsDuringParsing(warnings: Seq[ParseReport]) extends Message {
  override def toString: String = s"warnings_during_parsing(warnings=${warnings.toString})"
  override val name: String = s"warnings_during_parsing"
}

case class WarningsDuringTypechecking(warnings: Seq[TypecheckerWarning]) extends Message {
  override def toString: String = s"warnings_during_typechecking(warnings=${warnings.toString})"
  override val name: String = s"warnings_during_typechecking"
}

abstract class SimpleMessage(val text: String) extends Message {
  override def toString: String = s"$name(text=$text)"
  override val name: String = s"simple_message"
}

case class ConfigurationConfirmation(override val text: String) extends SimpleMessage(text) {
  override val name: String = s"configuration_confirmation"
}

case class InternalWarningMessage(override val text: String) extends SimpleMessage(text) {
  override val name: String = s"internal_warning_message"
}

case class CopyrightReport(override val text: String) extends SimpleMessage(text) {
  override val name: String = s"copyright_report"
}

case class MissingDependencyReport(override val text: String) extends SimpleMessage(text) {
  override val name = "missing_dependency_report"
}

// FIXME: for debug purposes only: a pong message can be reported to indicate
// FIXME: that the verification backend is alive.
case class PongMessage(override val text: String) extends SimpleMessage(text) {
  override val name: String = s"dbg__pong"
}
