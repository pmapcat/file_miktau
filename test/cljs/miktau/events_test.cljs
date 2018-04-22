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

(deftest test-filtering-and-clear []
  (let [db demo-data/initial-db-after-load-from-server]
    (is (= (:filtering (miktau-events/filtering db [nil "h"])) "h"))
    (is (= (:filtering (miktau-events/filtering db [nil ""])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil "hello"])) "hello"))
    (is (= (:filtering (miktau-events/filtering db [nil nil])) ""))
    (is (=
         (:filtering
          (-> (miktau-events/filtering db [nil "hello"])
              (miktau-events/filtering    [nil "blab"]))) "blab"))
    (is (=
         (:filtering
          (-> (miktau-events/filtering db [nil "hello"])
              (miktau-events/clear        [nil "blab"]))) ""))))

(deftest test-click-on-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :year :2010]))
      {:year 2010}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :drozd nil]))
      {}))
    
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :drozd -23]))
      {}))
    
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010}) [nil :year :2010]))
      {:year nil}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 19}) [nil :day :19]))
      {:year 2010 :day nil}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20}) [nil :day :19]))
      {:year 2010 :day 19}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20}) [nil :month :3]))
      {:year 2010 :day 20 :month 3}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20 :month 3}) [nil :month :3]))
      {:year 2010 :day 20 :month nil}))))

(deftest test-click-on-fast-access-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" {:year 2018 :month 3}]))
      {:year 2018 :month 3}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" nil]))
      {}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil nil nil]))
      {}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" {}]))
      {}))
    
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item (assoc  db :calendar-selected {:year 2018 :month 3}) [nil "FastAccess" {:year 2018 :month 3}]))
      {}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20 :month 3}) [nil "FastAccess" {:year 2018}]))
      {:year 2018}))))

(deftest test-click-on-cloud-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil :work])) #{:work}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud (assoc db :cloud-selected #{:zanoza}) [nil :work])) #{:zanoza :work}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud (assoc db :cloud-selected #{:work}) [nil :work])) #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil nil]))     #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil 123]))     #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil "graws"])) #{}))))


(deftest test-clicked-many-cloud-items []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items db [nil [:hello :world]])) #{:hello :world}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items db [nil [nil nil]])) #{}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil [:za :zo]])) #{:za :zo}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil (into '() [:za :zo])])) #{:za :zo}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil nil])) #{:ha :ho}))))
