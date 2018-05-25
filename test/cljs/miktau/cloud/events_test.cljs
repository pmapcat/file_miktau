(ns miktau.cloud.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.cloud.events :as miktau-events]
            [miktau.tools :as utils]
            [miktau.cloud.demo-data-test :as demo-data]
            [miktau.test-utils :refer [with-diff]]            
            [miktau.demo-data-test :refer [demo-response]]))




(deftest test-existence-of-utils-namespace []
  (is (= utils/in-utils-variable "BLAB")))

(deftest test-clicking-on-disabled-cloud-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{:hem :in :dal})]
    (is (=  (:cloud-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) #{:hello}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) {}))))

(deftest test-clicking-on-disabled-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{:hem :in :dal})]
    (is (=  (:calendar-selected (:db (miktau-events/click-on-disabled-calendar {:db  db} [nil :year 2018]))) {:year 2018}))))

(deftest test-getting-default-app-data []
  (with-diff
    (miktau-events/got-app-data demo-data/demo-db [nil  demo-response])
    demo-data/initial-db-after-load-from-server))


(deftest test-filtering []
  (let [db demo-data/initial-db-after-load-from-server]
    (is (= (:filtering (miktau-events/filtering db [nil "h"])) "h"))
    (is (= (:filtering (miktau-events/filtering db [nil ""])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil "****"])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil "bibilo@@@"])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil ""])) ""))
    
    (is (= (:filtering (miktau-events/filtering db [nil "hello"])) "hello"))
    (is (= (:filtering (miktau-events/filtering db [nil nil])) ""))
    (is (=
         (:filtering
          (-> (miktau-events/filtering db [nil "hello"])
              (miktau-events/filtering    [nil "blab"]))) "blab"))
    (is (=
         (:filtering
          (:db
           (-> (miktau-events/filtering db [nil "hello"])
               (miktau-events/clear        [nil "blab"])))) ""))))

(deftest test-click-on-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :year 2010]))) {:year 2010}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010})} [nil :year 2010]))) {}))
    
    
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :drozd nil]))) {}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :drozd -23]))) {}))
    
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010})} [nil :year 2010]))) {}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 19})} [nil :day 19]))) {:year 2010}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20})} [nil :day 19]))) {:year 2010 :day 19}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20})} [nil :month 3]))) {:year 2010 :day 20 :month 3}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20 :month 3})} [nil :month 3])))
           {:year 2010 :day 20}))))

(deftest test-click-on-fast-access-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" {:year 2018 :month 3}]))) {:year 2018 :month 3}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil nil nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" {}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc  db :calendar-selected {:year 2018 :month 3})} [nil "FastAccess" {:year 2018 :month 3}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20 :month 3})} [nil "FastAccess" {:year 2018}]))) {:year 2018}))))

(deftest test-click-on-cloud []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    ;; no selection is available when click happens
    ;; clear caching should happen also
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil :work]))) #{:work}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db (assoc db :cloud-selected #{:zanoza})} [nil :work]))) #{:zanoza :work}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db (assoc db :cloud-selected #{:work})} [nil :work]))) #{}))
    
    ;; nil test
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db nil} [nil :work]))) #{:work}))
    
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil nil])))     #{}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil 123])))     #{}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil "graws"]))) #{}))))
