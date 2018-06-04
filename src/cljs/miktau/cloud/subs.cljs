(ns miktau.cloud.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.meta-db :refer [meta-page?]]))

(defn get-db-for-test-purposes [db _]
  (if-not (meta-page? db :cloud)
    {}
    db))

(refe/reg-sub :cloud/get-db-for-test-purposes get-db-for-test-purposes)
(comment
  ;; (map keyword (:patriarchs @(refe/subscribe [:cloud/get-db-for-test-purposes])))
  
  
  )

(defn cloud
  "TESTED"
  [db _]
  (if-not (meta-page? db :cloud)
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
            :weighted-size (/ tag-size max-size)
            :disabled?      (not (contains? (:cloud-can-select db) tag))
            :selected?      (contains? (db :cloud-selected) tag)
            :can-select?    (contains? (:cloud-can-select db) tag)})))
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


