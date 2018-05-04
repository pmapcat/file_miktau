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

(deftest testing-is-this-tag-allowed? []
  (is (= (utils/allowed-tag-or? nil "**") "**"))
  (is (= (utils/allowed-tag-or? "" "**") "**"))
  (is (= (utils/allowed-tag-or? "-123" "**") "**"))
  (is (= (utils/allowed-tag-or? (apply str (take 100 (repeat "zanzibar"))) "**") "**"))
  (is (= (utils/allowed-tag-or? "Привет Мир" "**") "**"))
  (is (= (utils/allowed-tag-or? "Привет_Мир" "**") "Привет_Мир"))
  (is (= (utils/allowed-tag-or? "anachronizm222" "**") "anachronizm222"))
  (is (= (utils/allowed-tag-or? "heronimo-" "**") "**"))
  (is (= (utils/allowed-tag-or? "momavali_dro" "**") "momavali_dro"))
  (is (= (utils/allowed-tag-or-include-empty? "" "**") "")))

(deftest testing-find-all-tags-in-string? []
  (is (= (utils/find-all-tags-in-string  "ядлчосядчлосядлчосдляочсдлоялдочдсолчсоялосдляослдядчосдялчос
    Hello,blab, and$world и Яуза_Такая река$$$") ["Hello" "blab" "and" "world" "и" "Яуза_Такая" "река"]))
  (is (= (utils/find-all-tags-in-string  "" ) []))
  (is (= (utils/find-all-tags-in-string  "$$" ) []))
  (is (= (utils/find-all-tags-in-string  nil ) []))
  (is (= (utils/find-all-tags-in-string  47 ) [])))



