## Release 2020.7
#### Date 17/07/20     [Download](http://www.pm.inf.ethz.ch/research/viper/downloads.html)
##### Our repositories are now on GitHub: [github.com/viperproject](https://github.com/viperproject)

### Changes in Viper Language
* Axiom names are now optional [(Silver, 193)](https://github.com/viperproject/silver/issues/193).
For example, we now allow domains such as:
	```
    domain MyDomain {
      function foo(): Int

      axiom  { foo() > 2 }
      axiom  upperlimit { foo() < 8 }
    }
	```
* Experimental support for **termination checks** [tutorial section about termination](http://viper.ethz.ch/tutorial/#termination).
  * This extension provides support for specifying termination measures for heap-dependent functions and using them to prove termination.
  * There is also support for proving termination of methods and loops, but those are still experimental and support for these features as well as the semantics of the specifications might change.
  * Details are explained in the Viper tutorial.
* Experimental support for **counter examples**:
  * Added the command line option `--counterexample`. When using either backend with `--counterexamples=variables`, it will return, along with each verification error, a counter example for the values of all local variables (but not the heap or functions) on the Viper level. Alternatively, using the option `--counterexample=native` will return a backend-specific counter example along with each error that contains the entire model exactly as returned by the SMT solver, i.e., including any renaming and encoding the backends may have performed.

### Bug fixes
* 64-bit Z3 is now shipped with the Windows distribution (the 32-bit version which was inadvertently still being shipped can cause memory issues for some examples) [(Silicon, 375)](https://github.com/viperproject/silicon/issues/375)
* Intermittent Failures on Viper Online (web interface) should no longer happen (but for now we only support the default backend) [(Silver, 264)](https://github.com/viperproject/silver/issues/264)
* Reserved words are declared in only one place in the codebase [(Silver, 301)](https://github.com/viperproject/silver/issues/301)
* The phases used to organise programmatically calling verifiers (frontends, in the codebase) have been made more consistent and flexible [(Silver, 270)](https://github.com/viperproject/silver/issues/270)
* Several examples which had long runtimes are now apparently stable (but we would appreciate any feedback to the contrary!). e.g. [(Silver, 457)](https://github.com/viperproject/silver/issues/457)


### Viper Language API changes:
* The `DomainAxiom` AST node no longer exists, it was replaced by:
  * `NamedDomainAxiom`, providing exactly the same features as `DomainAxiom`.
  * `AnonymousDomainAxiom`, which is similar to the node above except for the absence of the `name` parameter.

### Backend-specific upgrades/changes

* [Symbolic Execution Verifier](https://bitbucket.org/viperproject/silicon/)
  * More-complete aliasing properties can be deduced in programs using quantified permissions [(Silicon, 502)](https://github.com/viperproject/silicon/issues/502)
  * Potential crash removed when using quantifiers and unfolding expressions in a function body [(Silicon, 491)](https://github.com/viperproject/silicon/issues/491)

* [Verification Condition Generation Verifier](https://bitbucket.org/viperproject/carbon/)
  * Axioms used for sequence operations should be more complete (especially for cases such as dropping more elements than a sequence has) [(Carbon, 36)](https://github.com/viperproject/carbon/issues/36)
  * Sequence contains function now works in triggers [(Carbon, 349)](https://github.com/viperproject/carbon/issues/349)
  * Carbon should now kill child Boogie and Z3 processes eagerly on stop(); this behaviour may be setup-dependent so please report any outstanding issues [(Carbon, 225)](https://github.com/viperproject/carbon/issues/225)
  * Predicates whose parameters include bound variables (e.g. from outer-quantifiers) now work in function specifications [(Carbon, 271)](https://github.com/viperproject/carbon/issues/271)
  * Work in progress on correct handling of perm-expressions inside unfolding expressions / quantifiers: some issues have been fixed; a summary of the current status is provided [(Carbon, 348)](https://github.com/viperproject/carbon/issues/348)

---

## Release 2020.1
#### Date 31/01/20     [Download](http://www.pm.inf.ethz.ch/research/viper/downloads.html)

### Changes in Viper Language
* Support for variable scopes. For example, now it is possible
to define two variables with the same name in different branches:
    ```
	if (b) {
        var x: Int := 5;
    } else {
        var x: Bool := true;
    }
	```
* Added support for using field lookups and predicates as triggers, e.g.
    ```
	forall r: Ref :: {r.f} r in s ==> r.f > 0

    forall r: Ref :: {P(r)} r in s ==> (unfolding P(r) in r.f > 0)
	```
    Note: this support still has some [issues](https://bitbucket.org/viperproject/carbon/issues/257) in Viper's VCG-based verifier.

* Added support for trigger annotations on existential quantifiers. These are written and behave analogously to those already supported on forall quantifiers, and are relevant in the logically dual cases, typically when *proving* an existentially-quantified assertion [(189)](https://bitbucket.org/viperproject/silver/issues/189).
* Extended support for Viper plugins (designed to make lightweight Viper front-end tools simple to write). In particular, extending the grammar of the parser via plugins is now possible.
* Removed the experimental built-in function termination checking. An experimental
termination plugin is available: https://bitbucket.org/viperproject/termination-plugin/
Feedback is welcome; we plan support for termination checking in the next release.
* Renamed all example files from .sil to .vpr [(287)](https://bitbucket.org/viperproject/silver/issues/287).
* The unary plus operator has been removed [(108)](https://bitbucket.org/viperproject/silver/issues/271).
* Whitespace between unary operations and their operands is no-longer allowed [(272)](https://bitbucket.org/viperproject/silver/issues/272).
* The `fresh` statement was removed [(87)](https://bitbucket.org/viperproject/silver/issues/87).
* Macros can now take field names as parameters [(170)](https://bitbucket.org/viperproject/silver/issues/170).
* LHS of field assignment statements can now be a macro invocation [(214)](https://bitbucket.org/viperproject/silver/issues/214).
* Warnings are now generated for unused macro parameters [(267)](https://bitbucket.org/viperproject/silver/issues/267).
* Macro definitions are disallowed inside magic wand proof scripts [(215)](https://bitbucket.org/viperproject/silver/issues/215).

### Bug fixes
* Conditional assertions as part of quantified permissions are properly supported, e.g. 
  `inhale forall j : Int :: (j == k ? acc(s.f) : true)` [(260)](https://bitbucket.org/viperproject/silver/issues/260).
* Expression macros with missing bodies no-longer cause spurious parser errors (for next construct) [(273)](https://bitbucket.org/viperproject/silver/issues/273).
* `new(..)` parameterised by identifiers which are present in the program but are not field names now produces an appropriate error message [(247)](https://bitbucket.org/viperproject/silver/issues/247).
* Type checker no longer crashes on ASTs with sharing [(191)](https://bitbucket.org/viperproject/silver/issues/191).
* Macro names are now checked for clashes [(167)](https://bitbucket.org/viperproject/silver/issues/167).
* The AST utility method `Expressions.instantiateVariables` is now capture-avoiding (an additional parameter can be provided to exclude certain names from being chosen when renaming bound variables) [(286)](https://bitbucket.org/viperproject/silver/issues/286).
* Eliminated spurious type error involving #E type when using empty Multisets via macros [(282)](https://bitbucket.org/viperproject/silver/issues/282).
* AST transformation framework's Rewritable is no-longer limited w.r.t. collections of Rewritables [(225)](https://bitbucket.org/viperproject/silver/issues/225).

### Viper Language API changes: 
* `LocalVar` AST node no longer has type as an optional field [(281)](https://bitbucket.org/viperproject/silver/issues/281).
* `Result` AST node no longer has type in its second parameter list [(283)](https://bitbucket.org/viperproject/silver/issues/283).
* `Call` trait, `FuncApp` and `DomainFuncApp` AST nodes no longer have a `formalArgs` field; this information can be obtained from the enclosing `Program` [(241)](https://bitbucket.org/viperproject/silver/issues/241).
* Upgraded all dependencies, libraries, plugins and development environment:
    * SBT 1.2.6, rewriting all build files.
	* Scala 2.12.7
	* fastparse	1.0.0
	* ScalaTest 3.0.5.
	* scallop 3.1.3.
	* jgrapht-core 1.2.0.
	* scala-parser-combinators 1.1.1.
	* commons-io 2.6.
	* guava 26.0.
	* slf4j-api 1.7.25.
	* logback-classic 1.2.3.
	* scala-logging 3.9.0
	* sbt-assembly 0.14.7
	* sbteclipse-plugin	5.2.4.
  

### Backend-specific upgrades/changes
* [Symbolic Execution Verifier](https://bitbucket.org/viperproject/silicon/)
    * Implemented an experimental feature that allows using the quantified
permissions based handling of permissions for all permissions. This
handling of permissions is expected to be more complete and may be faster;
however, the implementation is still in beta state. The feature can be
enabled with the ``--enableMoreCompleteExhale`` flag.
    * (API) Uses logback library instead of log4j for logging.
    * (API) Changed how the verifier reports errors: introduced a reporter class
that is responsible for reporting errors to the user (before errors were
reported via logging). Verification results of a particular method are
reported via the reporter as soon as they become available without
waiting for all methods to be verified.
* [Verification Condition Generation Verifier](https://bitbucket.org/viperproject/carbon/)
    * Quantified permissions with multiple forall-bound variables are now supported [(205)](https://bitbucket.org/viperproject/carbon/issues/205/).
    * Experimental adjustments to the background axioms used for set, sequence and multiset support. These should   mostly add completeness; fewer scenarios should require user assertions to verify. Please report any bugs observed (particularly examples which exhibit poorer performance). In the next release, this will be stabilised, and migrated analogously to Viper's Symbolic Execution back-end. 
    * Old expressions in triggers are now translated correctly [(236)](https://bitbucket.org/viperproject/carbon/issues/236/).
    * Substitution of actuals for formals in method specifications, predicate unfoldings and function preconditions is now capture-avoiding [(262)](https://bitbucket.org/viperproject/carbon/issues/262), [(282)](https://bitbucket.org/viperproject/carbon/issues/282).
    * Quantified predicates without arguments no-longer cause a crash [(279)](https://bitbucket.org/viperproject/carbon/issues/279).

### Other Notes
   * We observed that 'Out of memory' errors can occur for some larger examples if using a 32-bit (rather than 64-bit) version of Z3 [(286)](https://bitbucket.org/viperproject/carbon/issues/286). We recommend always using a 64-bit version.
