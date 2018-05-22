(ns miktau.edit-nodes.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.edit-nodes.events :as miktau-events]
            [miktau.edit-nodes.demo-data-test :as demo-data]))

(deftest test-delete-tag-from-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:blab}) [nil :hello])) #{:hello :blab}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection nil [nil :asd])) #{:asd}))
    
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil :hello])) #{:hello}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:hello}) [nil :hello])) #{}))
    
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil nil])) #{}))))

(deftest test-add-tag-to-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection (assoc db :nodes-temp-tags-to-add "zero vasya galibob") [nil "zab"])) "zab"))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil "blob"])) "blob"))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil nil])) ""))))

(deftest test-build-drill []
  (let [db {:nodes-temp-tags-to-add "blop glop" :nodes-temp-tags-to-delete #{:hom} :cloud-selected #{:hello :hom}}]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{:blop :glop :hello})))
  (let [db {:nodes-temp-tags-to-add "" :nodes-temp-tags-to-delete #{:hom :hello :dello} :cloud-selected #{:hello :hom}}]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{})))
  (let [db {:nodes-temp-tags-to-add "hello world" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{:hello :world})))
  (let [db {:nodes-temp-tags-to-add "hello" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{:hello})))
  (let [db nil]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{})))
  
  (let [db {:nodes-temp-tags-to-add "" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-updated-drilldown-on-nodes-or-cloud db))
           #{}))))

(deftest test-submit-tagging []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server
                    :nodes-selected #{"*"}
                    :cloud-selected #{:hello :hom}
                    :nodes-temp-tags-to-add "blop glop"
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add    (:db (miktau-events/submit-tagging {:db db} nil))) ""))
    (is (= (:nodes-temp-tags-to-delete (:db (miktau-events/submit-tagging {:db db} nil))) #{}))
    (is (= (:nodes-selected            (:db (miktau-events/submit-tagging {:db db} nil))) #{"*"}))
    (is (= (:cloud-selected            (:db (miktau-events/submit-tagging {:db db} nil))) #{:blop :glop :hello}))))

(deftest test-cancel-tagging []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"}
                    :nodes-temp-tags-to-add #{:blop}
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add     (:db (miktau-events/cancel-tagging  db nil))) ""))
    (is (= (:nodes-temp-tags-to-delete  (:db (miktau-events/cancel-tagging  db nil))) #{}))
    (is (= (:nodes-selected             (:db (miktau-events/cancel-tagging  db nil))) #{"*"}))
    (is (= (:nodes-selected             (:db (miktau-events/cancel-tagging  nil nil))) nil))))


