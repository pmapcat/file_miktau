(ns miktau.edit-nodes.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.edit-nodes.subs :as miktau-subs]
            [miktau.edit-nodes.demo-data-test :as demo-data]))


(deftest testing-nodes-changing
  []
  (is (= (miktau-subs/nodes-changing nil nil) {}))
  (is (= (miktau-subs/nodes-changing {:meta {:page :edit-nodes}} nil)
         {:all-selected? false, :total-amount 0, :tags-to-add nil, :tags-to-delete (list)}))
  
  (is (= (dissoc (miktau-subs/nodes-changing demo-data/initial-db-after-load-from-server nil) :tags-to-delete)
         {:all-selected? true, :total-amount 22, :tags-to-add ""}))

  
  
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
  ;; MOVED THIS FUNCTIONALITY TO THE SERVER SIDE
  ;; (is (= (first (:tags-to-delete (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-temp-tags-to-delete #{:работа_сделана}
  ;;                                                                   :nodes-selected #{"/home/mik/figuratively/blab.mp4"}) nil)))
  ;;        {:name "работа_сделана"
  ;;         :compare-name "работа_сделана",
  ;;         :key-name :работа_сделана,
  ;;         :selected? true
  ;;         :can-select? true}))
  
  ;; test "not showing" of the data
  (is
   (= (dissoc (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-selected #{}) nil) :tags-to-delete)
      {:all-selected? false, :total-amount 0, :tags-to-add ""})))
