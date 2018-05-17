(ns miktau.edit-nodes.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.edit-nodes.subs :as miktau-subs]
            [miktau.utils :as utils]
            [miktau.edit-nodes.events :as miktau-events]
            [clojure.data :as clojure-data]
            [miktau.edit-nodes.demo-data-test :as demo-data]))


(deftest null-testing []
  (is  (miktau-subs/filtering nil nil))
  (is  (miktau-subs/cloud nil nil))
  (is  (miktau-subs/calendar nil nil))
  (is  (miktau-subs/selection-cloud nil nil))
  (is  (miktau-subs/fast-access-calendar nil nil))
  (is  (miktau-subs/node-items nil nil))
  (is  (miktau-subs/nodes-changing nil nil)))

(deftest test-generate-tags-on-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"/home/mik/figuratively/dar.mp4"
                                                                                  "/home/mik/figuratively/gir.mp4"
                                                                                  "/home/mik/figuratively/grar.mp4"})]
    (is (=  (miktau-subs/generate-tags-on-selection db)
            #{:work :personal :blog :usecases}))
    (is (=  (miktau-subs/generate-tags-on-selection (assoc db :nodes-selected #{}))
            #{}))

    (is (=  (miktau-subs/generate-tags-on-selection (assoc db :nodes-selected #{"*"}))
            (into #{} (map keyword ["amazon" "bibliostore" "blog" "devops" "everybook" "moscow_market" "natan" "personal" "sforim" "translator" "UI" "usecases" "wiki" "work" "zeldin" "биржа" "магазины" "скачка_источников" "согласовать" "работа_сделана"]))))
    (is (=  (miktau-subs/generate-tags-on-selection nil)
            #{}))))

  
(deftest testing-nodes-changing
  []
  (is (= (dissoc (miktau-subs/nodes-changing demo-data/initial-db-after-load-from-server nil) :tags-to-delete)
         {:display? true, :all-selected? true, :total-amount 22, :tags-to-add ""}))
  
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing demo-data/initial-db-after-load-from-server nil)))
         {:name "amazon"
          :compare-name "amazon",
          :key-name :amazon,
          :selected? false,
          :can-select? true}))
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-temp-tags-to-delete #{:amazon}) nil)))
         {:name "amazon"
          :compare-name "amazon",
          :key-name :amazon,
          :selected? true
          :can-select? true}))
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-temp-tags-to-delete #{:работа_сделана}
                                                                    :nodes-selected #{"/home/mik/figuratively/blab.mp4"}) nil)))
         {:name "работа_сделана"
          :compare-name "работа_сделана",
          :key-name :работа_сделана,
          :selected? true
          :can-select? true}))
  ;; test "not showing" of the data
  (is
   (= (dissoc (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-selected #{}) nil) :tags-to-delete)
      {:display? false, :all-selected? false, :total-amount 0, :tags-to-add ""})))









