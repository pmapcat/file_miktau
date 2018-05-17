(ns miktau.edit-nodes.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.utils :refer [meta-page?]]))

(defn selection-mode? [db _] true)
(refe/reg-sub :edit-nodes/selection-mode? selection-mode?)
(refe/reg-sub :edit-nodes/get-db-for-test-purposes (fn [db _] db))

(comment
  (println  (str (:tree-tag  @(refe/subscribe [:edit-nodes/get-db-for-test-purposes])))))

(defn generate-tags-on-selection
  "TESTED"
  [db]
  (cond
    (contains? (:nodes-selected db) "*")
    (into #{} (keys (:cloud-can-select db)))
    (empty? (:nodes-selected db))
    #{}
    :else
    (let [selected-nodes (:nodes-selected db)]
      (->>
       (filter #(contains? selected-nodes (str (:file-path  %) (:name %))) (:nodes db))
       (map :tags)
       (flatten)
       (map keyword)
       (into #{})))))

(defn nodes-changing
  "TESTED"
  [db _]
  (if-not (meta-page? db :edit-nodes)
    {}
    (try
      (let [all-selected? (=  (first (db :nodes-selected)) "*")
            temp-tags-to-delete (:nodes-temp-tags-to-delete db)]
        {:display?  (not (empty? (db :nodes-selected)))
         :all-selected? all-selected?
         :total-amount
         (if all-selected? (db :total-nodes) (count (db :nodes-selected)))
         :tags-to-add    (db :nodes-temp-tags-to-add)
         :tags-to-delete
         (sort-by
          :compare-name
          (for [tag  (generate-tags-on-selection db)]
            {:name  (str (name tag))
             :compare-name (cljs-string/lower-case (str (name  tag)))
             :key-name tag
             :selected? (contains? temp-tags-to-delete tag)
             :can-select? true}))})
      (catch :default e {}))))

(refe/reg-sub :edit-nodes/nodes-changing nodes-changing)
