(ns miktau.cloud.subs
  (:require [re-frame.core :as refe]
            [clojure.string :as cljs-string]
            [miktau.meta-db :refer [meta-page?]]
            [miktau.tools :as utils]))

(defn get-db-for-test-purposes [db _]
  (if-not (meta-page? db :cloud)
    {}
    db))

(refe/reg-sub :cloud/get-db-for-test-purposes get-db-for-test-purposes)
(defn extract-chains
  [parent-names item]
  (let [cur-name (keyword (:name item))
        pa (conj parent-names cur-name)]
    [{:items pa
      :tag cur-name}
     (for [i (vals (:children item))]
       (extract-chains pa i))]))

(defn cloud-with-context [db _]
  (into
   {}
   (for [[k v] (group-by :tag (flatten (extract-chains #{} (:tree-tag db))))]
     [k  (disj (:items (first v)) :root)])))
(refe/reg-sub :cloud/cloud-with-context cloud-with-context)


;; the algorithm
;; if tag is selected, then the tree must show its children

(defn general-tree
  "TESTED"
  [item pad-level cloud-can-select cloud-selected]
  (if (empty? (:name item))
    []
    (let [keyworded   (keyword (:name item))
          can-select? (contains? cloud-can-select  keyworded)
          selected?   (contains? cloud-selected  keyworded)
          leaf?       (empty? (:children item))
          base   (str (:name item))]
      (flatten
       [{:name    base
         :compare-name (cljs-string/lower-case base)
         :key-name keyworded
         :size     1
         :weighted-size  1
         :header?        (and  selected? (not leaf?))
         :leaf?          leaf?
         :disabled?      (and (not can-select?) (not selected?))
         :selected?      selected?
         :pad-level      pad-level
         :pad-background-class (str "rise-to-shadow-" pad-level)
         :can-select?    can-select?}
        (cond
          (and (= keyworded :root) (zero? pad-level))
          (for [child (vals (:children item))]
            (general-tree child (inc pad-level) cloud-can-select cloud-selected))
          (empty? cloud-selected)
          (if (<  pad-level 1)
            (for [child (vals (:children item))]
              (general-tree child (inc pad-level) cloud-can-select cloud-selected))
            [])
          selected?
          (for [child (vals (:children item))]
            (general-tree child (inc pad-level) cloud-can-select cloud-selected))
          :else
          [])]))))


(defn general-tree-subscription
  [db _]
  (if-not (meta-page? db :cloud)
     []
     (rest (general-tree (:tree-tag db) 0 (into #{} (keys (:cloud-can-select db))) (:cloud-selected db)))))

;; (refe/reg-sub :cloud/general-tree general-tree-subscription)

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

(defn calendar
  "TESTED"
  [db _]
  (if-not (meta-page? db :cloud)
    {}
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
                 (let [parsed-name tag
                       can-select? (contains? (get (:calendar-can-select db) group-name) tag)]
                   {:name   (utils/pad parsed-name 2 "0") 
                    :key-name tag
                    :sort-name parsed-name
                    :size     tag-size
                    :group    group-name
                    :weighted-size (/ tag-size max-size)
                    :disabled?      (not can-select?)
                    :selected?      (= (get (:calendar-selected db) group-name) parsed-name)
                    :can-select?    can-select?}))))})]))
      (catch :default e
        {}))))

(refe/reg-sub :cloud/calendar calendar)

(defn fast-access-calendar
  "TESTED"
  [db _]
  (if-not (meta-page? db :cloud)
    []
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
      (catch :default e []))))

(refe/reg-sub :cloud/fast-access-calendar fast-access-calendar)

(defn breadcrumbs [db _]
  (if-not (meta-page? db :cloud)
    {}
    (let [calendar-crumb (fn [field]
                           (if-let [item (field (:calendar-selected db))]
                             {:name (str (name field) ": " (utils/pad item 2 0))
                              :on-click [:cloud/click-on-calendar-item field  item]} nil))
          ranker (:cloud db)
          selectable-items 
          (if (and (empty? (:cloud-selected db)) (empty? (:calendar-selected db)))
            (keys (:children (:tree-tag db)))
            (keys (:cloud-can-select  db)))]
      {:calendar
       (filter
        (comp not nil?)
        [(calendar-crumb :year)
         (calendar-crumb :month)
         (calendar-crumb :day)])
       :show-all? (:breadcrumbs-show-all? db)
       :can-expand? (> (count selectable-items) 8)
       :cloud-items
       (let [click-children (:cloud-selected  db)]
         (for [[index item]  (map list  (range) (:cloud-selected  db))]
           {:name (str (name item))  :on-click [:cloud/clicked-many-cloud-items (take (inc index) click-children)]}))
       :cloud-can-select
       (sort-by
        :rank
        (filter
         (comp not empty?)
         (for [item  selectable-items]
           (if (contains? (:cloud-selected db) item)
             {}
             {:name (str (name item)) :rank (- (item ranker)) :on-click [:cloud/clicked-cloud-item   item]}))))})))

(refe/reg-sub :cloud/breadcrumbs breadcrumbs)

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


