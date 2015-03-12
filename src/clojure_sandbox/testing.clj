(ns clojure-sandbox.testing
  (:require [clojure.test :refer :all]))

; For IntelliJ
(declare thrown?
         thrown-with-msg?)


; The `is` macro does basic assertions and outputs failure details

(is (= 4 (+ 2 2)))
; => true

(is (= 5 (+ 2 2)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (testing.clj:9)
; expected: (= 5 (+ 2 2))
; actual: (not (= 5 4))
; => false



; `thrown?` tests for expected exceptions being thrown

(is (thrown? NullPointerException (inc nil)))
; => #<NullPointerException java.lang.NullPointerException>

(is (thrown? NullPointerException (inc 1)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (testing.clj:20)
; expected: (thrown? NullPointerException (inc 1))
; actual: nil
; => nil



; `thrown-with-msg?` also checks exception message against regex

(is (thrown-with-msg? ArithmeticException #"zero" (/ 1 0)))
; => #<ArithmeticException java.lang.ArithmeticException: Divide by zero>


(is (thrown-with-msg? ArithmeticException #"zero" (inc (Long/MAX_VALUE))))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (testing.clj:36)
; expected: (thrown-with-msg? ArithmeticException #"zero" (inc (Long/MAX_VALUE)))
; actual: #<ArithmeticException java.lang.ArithmeticException: integer overflow>
; => #<ArithmeticException java.lang.ArithmeticException: integer overflow>



; `testing` macro allows assertions to be grouped.  Strings after 'testing'
; are appended onto the error messages
;
(testing "Strings"
  (testing "regex"
    (is (re-find #"foo" "foobar"))
    (is (re-find #"foo" "bar")))
  (testing ".contains"
    (is (.contains "foobar" "foo"))))

; FAIL in clojure.lang.PersistentList$EmptyList@1 (testing.clj:50)
; Strings regex
; expected: (re-find #"foo" "bar")
; actual: (not (re-find #"foo" "bar"))
; => true
; This returns true because that's the value of the ".contains" testing block



; `deftest` macro defines tests as a zero-arg function with some metadata
; marking it as a test

(deftest test-foo-passing
  (is (= 1 1)))
; => #'clojure-sandbox.testing/test-foo-passing

(deftest test-foo-failing
  (is (= 1 2)))
; => #'clojure-sandbox.testing/test-foo-failing

(test-foo-passing)
; => nil

(test-foo-failing)
; FAIL in (test-foo-failing) (testing.clj:71)
; expected: (= 1 2)
;   actual: (not (= 1 2))
; => nil

; The actual test function is in the `:test` key of the metadata map
(:test (meta #'test-foo-passing))

((:test (meta #'test-foo-passing)))
; => true

((:test (meta #'test-foo-failing)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (testing.clj:71)
; expected: (= 1 2)
;   actual: (not (= 1 2))
; => false



; Test Suites - calling other test functions within a test function
(deftest a
  (is (== 0 (- 3 2))))

(deftest b
  (a))

(deftest c
  (b))

(c)
; Note the 'stack' of test functions in the failure message
; FAIL in (c b a) (testing.clj:100)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; => nil



; Defining a `test-ns-hook` function means that this is the only test function
; called by `run-tests`.
#_(defn test-ns-hook
  []
  (c))

; Unmap `test-ns-hook` to return to 'old' behaviour
;(ns-unmap *ns* 'test-ns-hook)



; Sample fixture that just logs 'starting and ending' messages
(defn logging-fixture
  [f]
  (println "Starting test" f)
  (f)
  (println "Ending test" f))

(use-fixtures :each logging-fixture)
