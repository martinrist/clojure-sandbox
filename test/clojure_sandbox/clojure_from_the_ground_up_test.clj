(ns clojure-sandbox.clojure-from-the-ground-up-test
  (:use clojure.test
        clojure-sandbox.clojure-from-the-ground-up))

(def test-functions (apply juxt [simple-palindromic? recursive-palindromic?]))


(deftest palindromic?-tests
  (testing "Empty string is palindromic"
    (is (every? true? (test-functions ""))))
  (testing "Single character is palindromic"
    (is (every? true? (test-functions "a"))))
  (testing "Two identical characters are palindromic"
    (is (every? true? (test-functions "aa"))))
  (testing "Two non-identical characters are not palindromic"
    (is (every? false? (test-functions "ab"))))
  (testing "Three identical characters are palindromic"
    (is (every? true? (test-functions "aaa"))))
  (testing "Three characters with middle different are palindromic"
    (is (every? true? (test-functions "aba"))))
  (testing "Three different characters are not palindromic"
    (is (every? false? (test-functions "abc")))))
