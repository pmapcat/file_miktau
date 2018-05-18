(ns miktau.edit-nodes.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.meta-db :refer [meta-page?]]))

(defn selection-mode? [db _] true)
(refe/reg-sub :edit-nodes/selection-mode? selection-mode?)
(refe/reg-sub :edit-nodes/get-db-for-test-purposes (fn [db _] db))


(comment
  (println  (:meta @(refe/subscribe [:edit-nodes/get-db-for-test-purposes]))))

(defn nodes-changing
  "TESTED"
  [db _]
  (if-not (meta-page? db :edit-nodes)
    {}
    (let [all-selected? (contains? (db :nodes-selected) "*")
          temp-tags-to-delete (:nodes-temp-tags-to-delete db)]
      {:all-selected? all-selected?
       :total-amount
       (if all-selected? (db :total-nodes) (count (db :nodes-selected)))
       :tags-to-add    (db :nodes-temp-tags-to-add)
       :tags-to-delete
       (or
        (sort-by
         :compare-name
         (for [tag  (keys (:cloud-can-select db))]
           {:name  (str (name tag))
            :compare-name (cljs-string/lower-case (str (name  tag)))
            :key-name tag
            :selected? (contains? temp-tags-to-delete tag)
            :can-select? true})) [])})))

(refe/reg-sub :edit-nodes/nodes-changing nodes-changing)
