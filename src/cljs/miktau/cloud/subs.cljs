(ns miktau.cloud.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.tools :as utils]
            [miktau.meta-db :refer [meta-page?]]))

(defn get-db-for-test-purposes [db _]
  (if-not (meta-page? db :cloud)
    {}
    db))
(refe/reg-sub :cloud/get-db-for-test-purposes get-db-for-test-purposes)

;; (let [[a b] (cljs-string/split "@had:sad" #":")]
;;   (println a "   HOHOHOH  " b))

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
          :compare-name   (cljs-string/lower-case (str point))
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

;; (str (name (keyword "@asdasd: [asdasd-asd]-asd")))
(refe/reg-sub :cloud/meta-cloud meta-cloud)
(comment
  (def db @(refe/subscribe [:cloud/get-db-for-test-purposes]))
  (utils/filter-map-on-key utils/is-meta-tag? (:cloud db))
  @(refe/subscribe [:cloud/meta-cloud])
  )

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
      {:name "Open in a single folder"
       :on-click  [:cloud/file-op :symlinks]
       :disabled? (> amount 150)}
      {:name "Open each file individually"
       :on-click  [:cloud/file-op :filebrowser]
       :disabled? (> amount 20)}
      {:name "Open each in default program"
       :on-click  [:cloud/file-op :default]
       :disabled? (> amount 10)}]}))
(refe/reg-sub :cloud/nodes-selection nodes-selection-editable-view)


