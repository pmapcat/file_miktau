(ns miktau.cloud.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.tools :as utils]
            [miktau.meta-db :refer [meta-page?]]
            [miktau.cloud.misc :as cloud-misc]))

(defn meta-cloud
  [db _]
  (if-not (meta-page? db :cloud)
    []
    (->>
     (for [[item amount] (utils/filter-map-on-key utils/is-meta-tag? (:cloud db))]
       (let [[_ group point] (cljs-string/split (str item) #":")
             disabled?       (not (contains? (:cloud-can-select db) item))]
         {:group group
          :name  point
          :weighted-size 1
          :size amount
          :compare-name   (or (cloud-misc/meta-rank item) (cljs-string/lower-case (str point)))
          :on-click
          (if disabled?
            [:cloud/clicked-disabled-cloud-item item]
            [:cloud/clicked-cloud-item item])
          :disabled?      disabled?
          :selected?      (contains? (:cloud-selected db) item)
          :can-select?    (contains? (:cloud-can-select db) item)}))
     (sort-by :group)
     (group-by :group)
     (utils/map-val #(sort-by :compare-name %)))))
(refe/reg-sub :cloud/meta-cloud meta-cloud)
;; (str (name (keyword "@asdasd: [asdasd-asd]-asd")))

(defn cloud
  [db _]
  (if-not (meta-page? db :cloud)
    []
    (try
      (let [cloud (utils/filter-map-on-key (comp not utils/is-meta-tag?) (:cloud db))
            max-size (apply max (vals cloud))]
        (sort-by
         :compare-name
         (for [[tag tag-size] cloud]
           (let [disabled? (not (contains? (:cloud-can-select db) tag))]
             {:name           (str (name tag))
              :compare-name   (cljs-string/lower-case (str (name tag)))
              :key-name       tag
              :size           tag-size
              :weighted-size  (/ tag-size max-size)
              :on-click
              (if disabled?
                [:cloud/clicked-disabled-cloud-item tag]
                [:cloud/clicked-cloud-item tag])
              :disabled?      disabled?   
              :selected?      (contains? (db :cloud-selected) tag)
              :can-select?    (contains? (:cloud-can-select db) tag)}))))
      (catch :default e []))))

(refe/reg-sub :cloud/cloud cloud)

(defn empty-view
  [db _]
  (if-not (meta-page? db :cloud)
    {:show-empty-cloud false
     :show-empty-all   false}
    (let[cloud (utils/filter-map-on-key (comp not utils/is-meta-tag?) (:cloud db))
          empty-cloud? (zero? (count cloud))
          empty-nodes? (zero? (:total-nodes db))]
      {:show-empty-cloud (and (not empty-nodes? ) empty-cloud?)
       :show-filtering-view (not empty-nodes?)
       :show-bottom-view   (not empty-nodes?)
       :show-empty-all   empty-nodes?})))
(refe/reg-sub :cloud/empty-view empty-view)

(defn nodes-selection-editable-view
  [db _]
  (let [amount (:total-nodes db)]
    {:amount (:total-nodes db)
     :narrow-results {:name "Narrow results"
                      :on-click [:cloud/redirect-to-nodes true]
                      :disabled? false}
     :links
     [{:name "Edit tags on selection"
       :on-click  [:cloud/redirect-to-edit-nodes true]
       :disabled? false}
      {:name "Open in folder"
       :on-click  [:cloud/file-op :symlinks]
       :disabled? (> amount 150)}]}))
(refe/reg-sub :cloud/nodes-selection nodes-selection-editable-view)


