(ns miktau.nodes.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.tools :as utils]
            [miktau.meta-db :refer [meta-page?]]))

(defn selection-mode? [db _]
  (if-not (meta-page? db :nodes)
    false
    (not (empty? (:nodes-selected db)))))
(refe/reg-sub :nodes/selection-mode? selection-mode?)

(refe/reg-sub :nodes/get-db-for-test-purposes (fn [db _] db))
(comment
  (println   (:meta @(refe/subscribe [:nodes/get-db-for-test-purposes]))))

(defn node-items
  "TESTED"
  [db _]
  (if-not (meta-page? db :nodes)
    []
    (try
      (let [all-selected? (=  (first (:nodes-selected db)) "*")]
        {:ordered-by
         (utils/parse-sorting-field (:nodes-sorted db))
         :total-nodes (:total-nodes db)
         :omitted-nodes (-  (:total-nodes db) (count (:nodes db)))
         :all-selected? all-selected?
         :nodes
         (for [i (:nodes db )]
           (let [selected?
                 (if all-selected?
                   true
                   (contains? (:nodes-selected db )
                              (str (:file-path i) (:name i))))]
             {:selected? selected?
              :modified (i :modified)
              :id (i :id)
              :name (:name i)
              :all-tags (map keyword (i :tags))
              :file-path (i :file-path)
              :tags
              (map
               #(dissoc % :compare-name)
               (sort-by
                :compare-name
                (for [tag  (i :tags)]
                  {:name           (str (name tag))
                   :key-name       (keyword (str tag))
                   :compare-name   (cljs-string/lower-case (str (name tag)))
                   :to-add?        false
                   :to-delete?     false
                   :selected?      (contains? (:cloud-selected db) (keyword (str tag)))
                   :can-select?    true})))}))})
      (catch :default e {}))))

(refe/reg-sub :nodes/node-items node-items)
