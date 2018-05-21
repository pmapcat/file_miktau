(ns miktau.test-utils
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [clojure.data :as clojure-data]))

(defn with-diff
  [a b]
  (is (= a b)
      (let [bdiff (butlast (clojure-data/diff a b))]
        (str
         "============ DIFF DATA ON THIS TEST ======================\n"
         (first bdiff) "\n"
         (second bdiff) "\n"
         "====================== END ==============================="))))
