(ns miktau.app-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.app :as app]))



(deftest test-arithmetic []
  (is  (= true true) "This test should pass!"))
