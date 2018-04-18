(ns miktau.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.events :as miktau-events]
            [miktau.demo-data-test :as demo-data]))

(deftest test-getting-default-app-data []
  (is
   (= (miktau-events/got-app-data
       {:db miktau.events/demo-db}
       [nil  demo-data/demo-response])
      demo-data/initial-db-after-load-from-server)))




