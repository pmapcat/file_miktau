(ns miktau.edit-nodes.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.meta-db :refer [meta-page?]]))

(defn can-submit?
  [db _]
  (or (not (empty? (:nodes-temp-tags-to-delete db)))
      (not (empty? (:nodes-temp-tags-to-add    db)))))
(refe/reg-sub :edit-nodes/can-submit? can-submit?)

(refe/reg-sub
 :edit-nodes/show-warning?
 (fn [db _]
   (:show-warning? db)))

(defn cloud
  [db _]
  (if-not (meta-page? db :edit-nodes)
    []
    (try
      (let [cloud (:cloud db) 
            max-size (apply max (vals cloud))]
        (sort-by
         :compare-name
         (for [[tag tag-size] cloud]
           (let [disabled?    (not (contains? (:cloud-can-select db) tag))
                 selected?    (contains? (db :cloud-selected) tag)
                 to-delete?   (contains? (:nodes-temp-tags-to-delete db) tag)
                 to-add?      (contains? (:nodes-temp-tags-to-add db) tag)]
             {:name           (str (name tag))
              :compare-name   (cljs-string/lower-case (str (name tag)))
              :key-name       tag
              :size           tag-size
              :weighted-size  (/ tag-size max-size)
              :to-delete?     to-delete?
              :to-add?        to-add?
              :on-click       [:edit-nodes/tag-click tag]
              :disabled?      disabled?   
              :selected?      selected?   
              :can-select?    (contains? (:cloud-can-select db) tag)}))))
      (catch :default e []))))
(refe/reg-sub :edit-nodes/cloud cloud)

(defn breadcrumbs [db _]
  (if-not (meta-page? db :edit-nodes)
    {}
    {:tags-to-add
     (for [item (:nodes-temp-tags-to-add db)]
       {:name item
        :on-click [:edit-nodes/tag-click item]})
     :tags-to-delete
     (for [item (:nodes-temp-tags-to-delete db)]
       {:name item
        :on-click [:edit-nodes/tag-click item]})
     :total-records (:total-nodes db)}))
(refe/reg-sub :edit-nodes/breadcrumbs breadcrumbs)
