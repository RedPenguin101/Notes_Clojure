(ns chapter16)

"16: Thinking programs: searching, unification, logic programming, constraint programming"

"16.2 Thinking data via unification"

'(= ?something 2)

"This expression is true when ?something is bound to 2. Let's represent that as"

'{?something 2}

"What we'd like is a function satisfy1 which, when given 2 args, returns a map of bindings that that would satisfy (= arg1 arg2)"

"In logic programming, a TERM is a value (i.e. data rather than expression) - though it can contain unknown variables"

1       ; constant term
'?x     ; variable term
'(1 2)   ; ground term - contains no variables
'(1 ?x) ; term

"A BINDINGS MAP describes the valies needing assignment to logic variables to make two terms equal."

(defn lvar?
  "Determines if a value represents a logic variable (i.e. it's a symbol which starts with a ?)"
  [x]
  (boolean
    (when (symbol? x)
      (re-matches #"^\?.*" (name x)))))

(lvar? 2);; => false
(lvar? 'hello);; => false
(lvar? '?hello);; => true

(defn satisfy1
  [l r knowledge]
  (let [L (get knowledge l l)
        ;; if l is a key in knowledge, get the value, otherwise just use l
        R (get knowledge r r)]
    (cond
      (= L R)   knowledge
      (lvar? L) (assoc knowledge L R)
      (lvar? R) (assoc knowledge R L)
      :default  nil)))

"The premise here is that you build out your 'knowledge' (a bindings map) by providing information about the potential equality of two terms"

(satisfy1 1 1 {})
;; => {}

"If the two provided terms are already equal, you add nothing to your knowledge - this isn't new information."

(satisfy1 '?something 2 {})
;; => {?something 2}

"If either of the two provided terms is a variable, you add it to the knowledge, binding the variable to the other term. So the values of ?something that satisfy something=2 is {?something 2}"

(satisfy1 2 '?something {})
;; => {?something 2}

"The variable term is always the key in the binding"

(satisfy1 '?x '?y {})
;; => {?x ?y}

"here the meaning is that for ?x to equal ?y, the values of ?x and ?y must be the same. This might not seem interesting, but it's the key idea behind unification: we're defering the question of equality until we have more information"

(->> {}
     (satisfy1 '?x '?y)
     (satisfy1 '?x 1))
;; => {?x ?y, ?y 1}

"here we show that if ?x = ?y, and ?x = 1, then ?y = 1. The ?x = 1 knowledge isn't encoded yet."

"this version of satisfy extends the implementation to allow for nested and recursive definitions"

(defn satisfy
  [l r knowledge]
  (let [L (get knowledge l l)
        R (get knowledge r r)]
    (cond
      (not knowledge)     nil
      (= L R)             knowledge
      (lvar? L)           (assoc knowledge L R)
      (lvar? R)           (assoc knowledge R L)
      (every? seq? [L R]) (satisfy (rest L) (rest R)
                                   (satisfy (first L) (first R)
                                            knowledge))
      ;; if L and R are both sequences, do a pairwise comparison of
      ;; the terms in the sequence, building up knowledge as you go
      :default            nil)))

(satisfy '(1 2 3) '(1 ?x 3) {});; => {?x 2}

(satisfy '(?x 2 3 (4 5 ?z))
         '(1 2 ?y (4 5 6))
         {})
;; => {?x 1, ?y 3, ?z 6}

(satisfy '(?x 10000 3) '(1 2 ?y) {});; => nil

"What we are doing is building a DATA-PROGRAMMABLE ENGINE"

"16.2.2 Substitution"

"A Binding map can be considered as an environment for substituting in values"

(require '[clojure.walk :as walk])

(defn subst [term binds]
  (walk/prewalk
    (fn [element]
      (if (lvar? element)
        (or (binds element) element)
        element))
    term))

"prewalk here is doing tree traversal, examining each element in the term in turn and applying fn to it.

if the element is a logic variable, fn tries to replace it with it's binding in the binding map"

(subst '(1 ?x 3) '{?x 2});; => (1 2 3)
(subst '((((?x)))) '{?x 2});; => ((((2))))
(subst '{:a ?x :b [1 ?x 3]} '{?x 2});; => {:a 2, :b [1 2 3]}

(subst '(1 ?x 3) '{?y 2});; => (1 ?x 3)
"here there's not enough information to complete the substitution"

"walk preserves data types!"

(subst '[:html [:head [:title ?title] [:body [:h1 ?title]]]]
       '{?title "Hi!"})
;; => [:html [:head [:title "Hi!"] [:body [:h1 "Hi!"]]]]

"16.2.3 Unification"

"UNIFICATION is a function that takes two terms and unfies them in the empty context, finally returning a new substitution. It has 3 parts:
* Deriving a binding (satisfy)
* Substitution (subst)
* Melding two structures together (meld, below)

melding just runs satisfy (to build the bindings map) and then subst to find the replacements"

(defn meld [term1 term2]
  (->> {}
       (satisfy term1 term2)
       (subst term1)))

(meld '(1 ?x 3) '(1 2 ?y))
;; => (1 2 3)

"Unification is the most simple type of data programming, but we can't even solve simple algebraic equations like 5x - 2(x-5) = 4x with it"

"16.3 An Introduction to core.logic"
