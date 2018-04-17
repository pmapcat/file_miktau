(ns miktau.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.subs :as miktau-subs]))


(deftest test-cloud []
  (is (= (miktau-subs/cloud ))
      "Something foul is a float."))

