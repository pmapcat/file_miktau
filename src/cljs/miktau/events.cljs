(ns miktau.events
  (:require
   [miktau.utils :as utils]
   [re-frame.core :as refe]
   [clojure.set :as clojure-set]
   [miktau.query-building :as query-building]))

(def init-db
  {:loading? true
   :filtering ""
   :nodes-sorted ""
   :core-directory ""
   :date-now {}
   
   :nodes []
   :nodes-selected #{}
   :nodes-temp-tags-to-delete #{}
   :nodes-temp-tags-to-add    ""

   :tree-tag {}
   
   :cloud-selected #{}
   :cloud  {}
   :cloud-can-select {}
   
   :calendar-selected {}
   :calendar   {:year {}
                :month {}
                :day   {}}
   :calendar-can-select {:year {}
                         :month {}
                         :day   {}}})

(refe/reg-event-fx
 :init
 (fn [_ _]
   {:db (assoc init-db :loading? true :nodes-selected #{} :cloud-selected #{})
    :fx-redirect [:get-app-data]}))

(refe/reg-event-fx
 :mutable-server-operation
 (fn [{:keys [db]} [_ response]]
   (if-not (nil? (:error response))
     {:db (assoc db :loading? false)
      :fx-redirect [:get-app-data]
      :log!  (str response)}
     {:db (assoc db :loading? false)
      :fx-redirect [:get-app-data]})))

(refe/reg-event-fx
 :http-error
 (fn [{:keys [db]} [_ response]]
   {:db (assoc db :loading? false)
    :log!  (str response)}))

(defn get-app-data
  [{:keys [db]} _]
  {:db (assoc db :loading? true)
   :http-xhrio (utils/server-call  (query-building/build-get-app-data db) :got-app-data :http-error)})
(refe/reg-event-fx :get-app-data get-app-data)


(defn got-app-data
  "TESTED"
  [db [_ response]]
  (let [got-app-data-if-diff
        (fn [db key]
          (if-not (= (key db) (key response))
            (assoc db key  (key response))
            db))]
    (->
     (assoc db :loading? false)
     (got-app-data-if-diff :core-directory)         
     (got-app-data-if-diff :calendar-can-select)    
     (got-app-data-if-diff :total-nodes)            
     (got-app-data-if-diff :date-now)               
     (got-app-data-if-diff :nodes)                  
     (got-app-data-if-diff :calendar)               
     (got-app-data-if-diff :cloud)                  
     (got-app-data-if-diff :cloud-can-select)       
     (got-app-data-if-diff :tree-tag)               
     (got-app-data-if-diff :nodes-sorted))))

(refe/reg-event-db :got-app-data got-app-data)


(refe/reg-event-db
 :back
 (fn [db _]
   (.log js/console "registered <back> event")
   db))

(defn filtering
  "TESTED"
  [db [_ data]]
  (assoc db :filtering
         (utils/allowed-tag-or-include-empty? (str data) (db :filtering))
         (str data)))
(refe/reg-event-db :filtering filtering)

(defn clear
  "TESTED"
  [db _]
  {:db 
   (assoc db
          :filtering ""
          :cloud-selected #{})
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :clear clear)

(defn click-on-fast-access-item
  "TESTED"
  [db group item]
  (let [already-selected (:calendar-selected db)]
    (cond
      (nil? item) db
      (= already-selected item)
      (assoc db :calendar-selected {})
      :else
      (assoc db :calendar-selected item))))

(defn click-on-calendar-item
  "TESTED"
  [{:keys [db]} [_ group key-name]]
  {:db
   (if (=  group "FastAccess")
     (click-on-fast-access-item db group key-name)
     (try
       (let [item (utils/mik-parse-int (name key-name) nil)
             already-has-item (get-in db [:calendar-selected group])]
         (cond
           (and  item (> item 0) (= already-has-item item))
           (assoc-in db [:calendar-selected group] nil)
           (and  item (> item 0))
           (assoc-in db [:calendar-selected group] item)
           :else
           db))
       (catch :default e
         db)))
   :fx-redirect [:get-app-data]})

(refe/reg-event-fx :click-on-calendar-item  click-on-calendar-item)
(defn discard-selection
  [db]
  (assoc db :cloud-selected #{}
         :calendar-selected {}
         :nodes-selected #{}))

(defn click-on-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (assoc
    db
    :cloud-selected
    (let [cloud-selected (into #{} (:cloud-selected db #{}))]
      (cond
        (and  (keyword? item) (contains? cloud-selected item))
        (disj cloud-selected item)
        (keyword? item)
        (conj cloud-selected item)
        :else
        cloud-selected)))
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :clicked-cloud-item  click-on-cloud)
(defn click-on-disabled-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (if (keyword? item)
     (assoc  (discard-selection db) :cloud-selected #{item})
     db)
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :clicked-disabled-cloud-item  click-on-disabled-cloud)

(defn click-on-disabled-calendar
  "TESTED"
  [{:keys [db]} [_ group item]]
  {:db
   (:db (click-on-calendar-item {:db (discard-selection db)} [nil group item]))
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :clicked-disabled-calendar-item  click-on-disabled-calendar)


(defn clicked-many-cloud-items
  "TESTED"
  [{:keys [db]} [_ items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     (= (:cloud-selected db) (into #{} items))
     (assoc  db :cloud-selected #{})
     :else
     (assoc db :cloud-selected (into #{} items)))
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :clicked-many-cloud-items  clicked-many-cloud-items)

(defn select-all-nodes
  "TESTED"
  [db _]
  (cond
    (contains? (:nodes-selected  db) "*")
    (assoc db :nodes-selected  #{})
    :else
    (assoc db :nodes-selected #{"*"})))
(refe/reg-event-db :select-all-nodes select-all-nodes)

(defn sort-nodes
  "TESTED"
  [{:keys [db]} [_ sort-order]]
  {:db
   (if (contains? #{"name" "-name" "modified" "-modified"} (str sort-order))
     (assoc db :nodes-sorted (str sort-order))
     (assoc  db :nodes-sorted "name"))
   :fx-redirect [:get-app-data]})
(refe/reg-event-fx :sort  sort-nodes)

(defn select-node
  "TESTED"
  [db [_ file-path]]
  (let [nodes-selected (into #{} (or (:nodes-selected db) #{}))]
    (cond
      (not  (string? file-path)) db
      (contains? nodes-selected "*")
      (assoc db :nodes-selected #{file-path})
      (contains? nodes-selected file-path)
      (assoc db :nodes-selected (disj nodes-selected file-path))
      :else
      (assoc db :nodes-selected (conj nodes-selected file-path)))))

(refe/reg-event-db :select-node select-node)

(defn only-select-node
  "TESTED"
  [db [_ file-path]]
  (cond
    (not  (string? file-path)) db
    (contains? (:nodes-selected db) "*")
    (assoc db :nodes-selected #{file-path})
    :else
    (update db :nodes-selected conj file-path)))

(refe/reg-event-db :select-only-select-node only-select-node)
(defn ^:export call_select_only_select_node [fpath]
  (refe/dispatch [:select-only-select-node fpath]))


(defn file-operation-fx
  "TESTED"
  [{:keys [db]} [_ operation-name]]
  (if-let [api-call (query-building/build-bulk-operate-on-files db operation-name nil)]
    {:http-xhrio (utils/server-call api-call :mutable-server-operation :http-error)
     :db db}
    {:db db}))
(refe/reg-event-fx :file-operation file-operation-fx)

(defn delete-tag-from-selection
  "TESTED"
  [db [_ tag-item]]
  (let [node-temp-tags (into #{} (or (:nodes-temp-tags-to-delete db) #{}))]
    (assoc
     db
     :nodes-temp-tags-to-delete
     (cond
       (and (keyword? tag-item) (contains? node-temp-tags tag-item))
       (disj node-temp-tags tag-item)
       (keyword? tag-item)
       (conj node-temp-tags tag-item)
       :else
       node-temp-tags))))
;; (contains? (list 1 2 3) 2)
(refe/reg-event-db :delete-tag-from-selection delete-tag-from-selection)

(defn add-tag-to-selection
  "TESTED"
  [db [_ tag-item]]
  (if (string? tag-item)
    (assoc db :nodes-temp-tags-to-add tag-item)
    db))
(refe/reg-event-db :add-tags-to-selection add-tag-to-selection)

(defn build-drill
  "TESTED"
  [db]
  (->>
   (clojure-set/difference
    (:cloud-selected db)
    (:nodes-temp-tags-to-delete db))
   (clojure-set/union
    (into #{} (map keyword (utils/find-all-tags-in-string (:nodes-temp-tags-to-add db)))))
   (assoc db :cloud-selected)))
;; (clojure-set/difference #{:hello :hom} #{:hom :didiom} )

(defn submit-tagging-fx
  "TESTED"
  [{:keys [db]} _]
  (if-let [api-call (query-building/build-update-records db nil)]
    {:db (assoc
          db
          :nodes-temp-tags-to-add ""
          :nodes-temp-tags-to-delete #{}
          :cloud-selected  (:cloud-selected (build-drill db))
          :nodes-selected #{})
     :http-xhrio (utils/server-call api-call :mutable-server-operation :http-error)}
    {:db db}))
(refe/reg-event-fx :submit-tagging submit-tagging-fx)

(defn cancel-tagging
  "TESTED"
  [db _]
  (assoc
   db
   :nodes-temp-tags-to-add ""
   :nodes-temp-tags-to-delete #{}
   :nodes-selected #{}))
(refe/reg-event-db :cancel-tagging cancel-tagging)
