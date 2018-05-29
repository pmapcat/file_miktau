(ns miktau.breadcrumbs.subs
  (:require [miktau.meta-db :refer [meta-page?]]
            [re-frame.core :as refe]))

(defn breadcrumbs [db _]
  (if-not (or (meta-page? db :cloud) (meta-page? db :nodes))
    {}
    (let [ranker (:cloud db)
          selectable-items 
          (if (and (empty? (:cloud-selected db)))
            (keys (:children (:tree-tag db)))
            (keys (:cloud-can-select  db)))]
      {:show-all? (:breadcrumbs-show-all? db)
       :can-expand? (> (count selectable-items) 8)
       :cloud-items
       (let [click-children (:cloud-selected  db)]
         (for [[index item]  (map list  (range) (:cloud-selected  db))]
           {:name (str (name item))  :on-click [:breadcrumbs/clicked-many-cloud-items (take (inc index) click-children)]}))
       :cloud-can-select
       (sort-by
        :rank
        (filter
         (comp not empty?)
         (for [item  selectable-items]
           (if (contains? (:cloud-selected db) item)
             {}
             {:name (str (name item)) :rank (- (item ranker)) :on-click [:breadcrumbs/clicked-cloud-item   item]}))))})))
(refe/reg-sub :breadcrumbs/breadcrumbs breadcrumbs)
