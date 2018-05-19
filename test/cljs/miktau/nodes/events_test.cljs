(ns miktau.nodes.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.nodes.events :as miktau-events]
            [cljs.test :as t]
            [miktau.nodes.demo-data-test :as demo-data]))

(deftest test-initialize-page []
  (is (= (:db (miktau-events/init {:db (dissoc demo-data/demo-db :meta)} nil nil nil))
         {:nodes-sorted "", :nodes [], :nodes-selected {},
          :meta {:page :nodes, :loading? false}, :total-nodes 0, :cloud-selected #{}, :calendar-selected {}})))


(deftest test-select-all-nodes []
  (let [db (assoc demo-data/initial-db-after-load-from-server :nodes-selected #{})]
    
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes nil [nil nil])) #{"*"}))
    ;; if nothing selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes db [nil nil])) #{"*"}))
    ;; if aleready something selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"blab" "blip"}) [nil nil])) #{"*"}))
    ;; if already all selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"*"}) [nil nil])) #{}))
    ;; if already all selected, but some are not
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"diro" "doro" "*"}) [nil nil])) #{}))))

(deftest test-sort-nodes []
  (let [db  demo-data/initial-db-after-load-from-server]
    
    ;; if nothing selected
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "-name"]))) "-name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil nil]))) "name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil :kliqo]))) "name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "modified"]))) "modified"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "-modified"]))) "-modified"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "name"]))) "name"))))

(deftest test-select-node []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{})]
    (is (=  (:nodes-selected  (miktau-events/select-node nil [nil "blab"])) #{"blab"}))
    ;; if nothing selected
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil nil])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil "hello"])) #{"hello"}))
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil nil])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"hello"}) [nil "hello"])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"hello"}) [nil "world"])) #{"hello" "world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"*"}) [nil "world"])) #{"world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"*" "zizo"}) [nil "world"])) #{"world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"zizo"}) [nil "zizo"])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"zizo"}) [nil "blab"])) #{"zizo" "blab"}))))


(deftest test-click-on-fast-access-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db db} [nil  {:year 2018 :month 3}]))) {:year 2018 :month 3}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db db} [nil  nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db db} [nil  nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db db} [nil  {}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db (assoc  db :calendar-selected {:year 2018 :month 3})} [nil  {:year 2018 :month 3}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-fast-access-item {:db (assoc db :calendar-selected {:year 2010 :day 20 :month 3})} [nil  {:year 2018}]))) {:year 2018}))))

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
