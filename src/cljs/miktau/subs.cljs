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


(defn cloud
  "TESTED"
  [db _]
  (if-not (can-use? db)
    []
    (for [[group-name group] (:cloud db)]
      (let [max-size (apply max (vals group))
            filterer (cloud-filtering-should-display? db) 
            should-display?
            (fn [group]
              (filter filterer group))
            sorting #(reverse (sort :key-name %))]
        {:group-name (str (name group-name))
         :max-size   max-size
         :group
         (sorting
          (should-display?
           (for [[tag tag-size] group]
             {:name    (str (name tag))
              :compare-name (cljs-string/lower-case (str (name tag)))
              :key-name tag
              :size     tag-size
              :group    group-name
              :weighted-size (/ tag-size max-size)
              :selected?      (contains? (db :selected) tag)
              :can-select?    (contains? (:cloud-can-select db) tag)})))}))))
(refe/reg-sub :cloud cloud)

(defn calendar
  "TESTED"
  [db _]
  (if-not (can-use? db)
    {}
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
               {:name    (if (=  group :month)
                           (utils/month-name tag)
                           (str (name tag))) 
                :key-name tag
                :sort-name (utils/mik-parse-int  (str (name tag)) 0)
                :size     tag-size
                :group   group-name
                :weighted-size (/ tag-size max-size)
                :selected?      (= (get (:calendar-selected db) group-name) tag)
                :can-select?    (contains? (get (db :calendar-can-select) group-name) tag)})))})]))))
(refe/reg-sub :calendar calendar)


(defn selection-cloud
  "TESTED"
  [db _]
  (if-not (can-use? db)
    []
    (if (empty? (:cloud-can-selected db))
      []
      (for [[tag _] (:cloud-can-select db )]
        {:name    (str (name tag))
         :key-name tag
         :weighted-size 1
         :selected?      (contains? (db :selected) tag)
         :can-select?    true}))))
(refe/reg-sub :selection-cloud selection-cloud)
(defn fast-access-calendar
  "TESTED"
  [db _]
  (if-not (can-use? db)
    []
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
      [])))
(refe/reg-sub :fast-access-calendar fast-access-calendar)

(defn node-items
  "TESTED"
  [db _]
  (if-not (can-use? db)
    {}
    (let [all-selected? (=  (first (:nodes-selected db)) "*")]
      {:ordered-by
       (utils/parse-sorting-field (:nodes-sorted db))
       :total-nodes (:total-nodes db)
       :ommitted-nodes (-  (:total-nodes db) (count (:nodes db)))
       :all-selected? all-selected?
       :nodes
       (for [i (:nodes db )]
         {:selected?
          (if all-selected?
            true
            (contains? (:nodes-sorted db )
                       (:file-path i)))
          :modified
          (i :modified)
          :id (i :id)
          :name (:name i)
          :all-tags (map keyword (i :tags))
          :file-path (i :file-path)
          :tags
          (for [tag (into #{} (concat  (i :tags) (:nodes-temp-tags-to-add db)))]
            {:name    (str (name tag))
             :key-name (keyword (str tag))
             :to-add?        (contains? (:nodes-temp-tags-to-add db) tag)
             :to-delete?     (contains? (:nodes-temp-tags-to-delete db) tag)
             :selected?      (contains? (:selected db) (keyword tag))
             :can-select?    true})})})))
(refe/reg-sub :node-items node-items)

(defn nodes-changing
  "TESTED"
  [db _]
  (if-not (can-use? db)
    {}
    (let [all-selected? (=  (first (db :nodes-selected)) "*")]
      {:display?  (not (empty? (db :nodes-selected)))
       :all-selected? all-selected?
       :total-amount
       (if all-selected? (db :total-nodes)
           (count (db :nodes-selected)))
       :tags-to-add    (db :nodes-temp-tags-to-add)
       :tags-to-delete (db :nodes-temp-tags-to-delete)})))

(refe/reg-sub :nodes-changing nodes-changing)
(comment
  ;; @(refe/subscribe [:cloud])
  )

