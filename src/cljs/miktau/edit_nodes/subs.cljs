(ns miktau.edit-nodes.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.meta-db :refer [meta-page?]]))

(defn selection-mode? [db _] true)
(refe/reg-sub :edit-nodes/selection-mode? selection-mode?)
(refe/reg-sub :edit-nodes/get-db-for-test-purposes (fn [db _] db))


(comment
  (println  (:meta @(refe/subscribe [:edit-nodes/get-db-for-test-purposes]))))

(defn cloud
  "TESTED"
  [db _]
  (if-not (meta-page? db :edit-nodes)
    []
    (try
      (let [max-size (apply max (vals (:cloud db)))]
        (sort-by
         :compare-name
         (for [[tag tag-size] (:cloud db)]
           {:name    (str (name tag))
            :compare-name (cljs-string/lower-case (str (name tag)))
            :key-name tag
            :size     tag-size
            :weighted-size  (/ tag-size max-size)
            :to-delete? (contains? (:nodes-temp-tags-to-delete db) tag)
            :to-add?    (contains? (:nodes-temp-tags-to-add db) tag)
            
            :disabled?      (not (contains? (:cloud-can-select db) tag))
            :selected?      (contains? (db :cloud-selected) tag)
            :can-select?    (contains? (:cloud-can-select db) tag)})))
      (catch :default e []))))
(refe/reg-sub :edit-nodes/cloud cloud)

(defn breadcrumbs [db _]
  (if-not (meta-page? db :edit-nodes)
    {}
    {:tags-to-add
     (for [item (:nodes-temp-tags-to-add db)]
       {:name item
        :on-click [:edit-nodes/add-tag-to-selection item]})
     :tags-to-delete
     (for [item (:nodes-temp-tags-to-delete db)]
       {:name item
        :on-click [:edit-nodes/delete-tag-from-selection item]})
     :total-records (:total-nodes db)}))
(refe/reg-sub :edit-nodes/breadcrumbs breadcrumbs)
