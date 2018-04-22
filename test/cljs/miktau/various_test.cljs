(ns miktau.various-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.utils :as utils]))

(deftest testing-parse-int? []
  (is (= (utils/mik-parse-int 123 -1) 123))
  (is (= (utils/mik-parse-int nil -1) -1))
  (is (= (utils/mik-parse-int -134 -1) -134))
  (is (= (utils/mik-parse-int :keyname -1) -1))
  (is (= (utils/mik-parse-int {} -1) -1))
  (is (= (utils/mik-parse-int "gradomysle" -1) -1))
  (is (= (utils/mik-parse-int "grado222mysle" -1) -1))
  (is (= (utils/mik-parse-int "--222" -1) -1))
  (is (= (utils/mik-parse-int "xzkjxzkcjzxkcj" -1) -1))
  (is (= (utils/mik-parse-int "-00001234" -1) -1234))
  (is (= (utils/mik-parse-int NaN -1) -1))
  (is (= (utils/mik-parse-int Infinity -1) -1))
  (is (= (utils/mik-parse-int -Infinity -1) -1)))

(deftest testing-seq-of-predicate? []
  (is (= (utils/seq-of-predicate? nil keyword?) false))
  (is (= (utils/seq-of-predicate? [] keyword?) false))
  (is (= (utils/seq-of-predicate? [:asd "asd"] keyword?) false))
  (is (= (utils/seq-of-predicate? [:asd :bsd :csd] keyword?) true)))

