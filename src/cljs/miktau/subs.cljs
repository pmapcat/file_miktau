(ns miktau.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [clojure.set :as clojure-set]

            [miktau.utils :as utils]))
(defn filtering [db _]
  (or (:filtering db) ""))

(defn can-use? [db]
  (let [loading (:loading? db)]
    (condp = loading
        nil false
        true false
        false true
        false)))
(defn selection-mode? [db _]
  (not (empty? (:nodes-selected db))))
(refe/reg-sub :selection-mode? selection-mode?)

(defn cloud-filtering-should-display?
  [db]
  (if (empty? (:filtering  db))
    (fn [_] true)
    (let [compara (cljs-string/lower-case (str (:filtering db)))]
      (fn [item]
        (cljs-string/includes? (str (:compare-name item)) compara)))))


(refe/reg-sub :filtering filtering)

(refe/reg-sub
 :get-db-for-test-purposes
 (fn [db _]
   db))
(comment
  (keys  @(refe/subscribe [:get-db-for-test-purposes])))

(defn cloud
  "TESTED"
  [db _]
  (try
    (for [[group-name group] (:cloud db)]
      (let [max-size (apply max (vals group))
            filterer (cloud-filtering-should-display? db) 
            should-display?
            (fn [group]
              (filter filterer group))]
        {:group-name (str (name group-name))
         :max-size   max-size
         :group
         (sort-by
          :compare-name
          (should-display?
           (for [[tag tag-size] group]
             {:name    (str (name tag))
              :compare-name (cljs-string/lower-case (str (name tag)))
              :key-name tag
              :size     tag-size
              :group    group-name
              :weighted-size (/ tag-size max-size)
              :disabled?      (not (contains? (:cloud-can-select db) tag))
              :selected?      (contains? (db :cloud-selected) tag)
              :can-select?    (contains? (:cloud-can-select db) tag)})))}))
    (catch :default e [])))

(refe/reg-sub :cloud cloud)

(defn calendar
  "TESTED"
  [db _]
  (try
    (into
     {}
     (for [[group-name group] (:calendar db)]
       [group-name
        (let [max-size (apply max (vals group))
              sorter-applicator
              (fn [data]
                (if (= group-name :year)
                  (reverse (sort-by :sort-name data))
                  (sort-by :sort-name data)))]
          {:group-name (str (name group-name))
           :max-size max-size
           :group
           (map
            #(dissoc % :sort-name)
            (sorter-applicator
             (for [[tag tag-size] group]
               (let [parsed-name (utils/mik-parse-int  (str (name tag)) 0)
                     can-select? (contains? (get (:calendar-can-select db) group-name) tag)]
                 {:name   (utils/pad (str (name tag)) 2 "0") 
                  :key-name tag
                  :sort-name parsed-name
                  :size     tag-size
                  :group   group-name
                  :weighted-size (/ tag-size max-size)
                  :disabled?      (not can-select?)
                  :selected?      (= (get (:calendar-selected db) group-name) parsed-name)
                  :can-select?    can-select?}))))})]))
    (catch :default e
      (println e)
      {})))

(refe/reg-sub :calendar calendar)

(defn selection-cloud
  "TESTED"
  [db _]
  (cond
    (empty? (:cloud-can-select db)) []
    (empty? (:cloud-selected  db))  []
    :else
    (sort-by
     :compare-name
     (let [filterer (cloud-filtering-should-display? db) 
           should-display?
           (fn [group]
             (filter filterer group))]
       (should-display?
        (for [[tag _] (:cloud-can-select db )]
          {:name    (str (name tag))
           :compare-name (cljs-string/lower-case (str (name tag)))
           :key-name tag
           :weighted-size  1
           :size           1
           :selected?      (contains? (db :cloud-selected) tag)
           :can-select?    true}))))))

(refe/reg-sub :selection-cloud selection-cloud)
(defn fast-access-calendar
  "TESTED"
  [db _]
  (try
    (if (:date-now db)
      [{:name "Today"
        :group "FastAccess"
        :can-select? (utils/is-it-today? db [:day :month :year])
        :key-name      (:date-now db)
        :selected? false}
       {:name "This month"
        :group "FastAccess"
        :can-select? (utils/is-it-today? db [:month :year])
        :key-name      (dissoc (:date-now db) :day)
        :selected? false}
       {:name "This year"
        :group "FastAccess"
        :can-select? (utils/is-it-today? db [:year])
        :key-name      {:year (:year (:date-now db))}
        :selected? false}]
      [])
    (catch :default e [])))
(refe/reg-sub :fast-access-calendar fast-access-calendar)

(defn node-items
  "TESTED"
  [db _]
  (try
    (let [all-selected? (=  (first (:nodes-selected db)) "*")
          tags-to-delete  (:nodes-temp-tags-to-delete db)
          newly-added-tags (utils/find-all-tags-in-string (:nodes-temp-tags-to-add db))]
      {:ordered-by
       (utils/parse-sorting-field (:nodes-sorted db))
       :total-nodes (:total-nodes db)
       :ommitted-nodes (-  (:total-nodes db) (count (:nodes db)))
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
              (if-not selected?
                (for [tag  (i :tags)]
                  {:name           (str (name tag))
                   :key-name       (keyword (str tag))
                   :compare-name   (cljs-string/lower-case (str (name tag)))
                   :to-add?        false
                   :to-delete?     false
                   :selected?      (contains? (:cloud-selected db) (keyword (str tag)))
                   :can-select?    true})
                (concat
                 (for [tag  (i :tags)]
                   {:name           (str (name tag))
                    :key-name       (keyword (str tag))
                    :compare-name   (cljs-string/lower-case (str (name tag)))
                    :to-add?        false
                    :to-delete?     (contains? tags-to-delete  (keyword (str tag)))
                    :selected?      (contains? (:cloud-selected db) (keyword (str tag)))
                    :can-select?    true})
                 (for [tag  newly-added-tags]
                   {:name           (str (name tag))
                    :key-name       (keyword (str tag))
                    :to-add?        true
                    :compare-name   (cljs-string/lower-case (str (name tag)))
                    :to-delete?     false
                    :selected?      false
                    :can-select?    false})))))}))})
    (catch :default e {})))


(refe/reg-sub :node-items node-items)

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
    (catch :default e {})))
(refe/reg-sub :nodes-changing nodes-changing)

