(ns miktau.cloud.events
  (:require
   [miktau.tools :as utils]
   [miktau.cloud.db :as miktau-db]
   [miktau.meta-db :as meta-db]
   [re-frame.core :as refe]))

(defn init
  "TODO: TEST"
  [_ [_ cloud-selected-set calendar-selected-dict]]
  {:db
   (assoc miktau-db/default-db
          :meta
          (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :cloud) true)
          :cloud-selected (or cloud-selected-set #{})
          :calendar-selected (or  calendar-selected-dict {}))
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/init-page init)

(defn redirect-to-nodes
  [{:keys [db]} [_ all-nodes-selected?]]
  {:db  db
   :fx-redirect [:nodes/init-page (if all-nodes-selected? #{"*"} #{}) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :cloud/redirect-to-nodes redirect-to-nodes)

(defn redirect-to-nodes-edit
  [{:keys [db]} [_ all-nodes-selected?]]
  {:db  db
   :fx-redirect [:edit-nodes/init-page (if all-nodes-selected? #{"*"} #{}) (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :cloud/redirect-to-edit-nodes redirect-to-nodes)

(defn file-op
  [{:keys [db]} [_ action]]
  {:db db
   :fx-redirect [:api-handler/file-operation :cloud/get-app-data action #{"*"} (:cloud-selected db) (:calendar-selected db)]})
(refe/reg-event-fx :cloud/file-op file-op)


(defn clear
  "TESTED"
  [{:keys [db]}  _]
  {:db 
   (assoc db
       :filtering ""
       :cloud-selected #{}
       :calendar-selected {})
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/clear clear)

(defn filtering
  "TESTED"
  [db [_ data]]
  (assoc db :filtering
         (utils/allowed-tag-or-include-empty? (str data) (db :filtering))
         (str data)))
(refe/reg-event-db :cloud/filtering filtering)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db
   :fx-redirect [:api-handler/get-app-data :cloud/got-app-data "" #{} (:cloud-selected db) (:calendar-selected db) {:page-size 1}]})
(refe/reg-event-fx :cloud/get-app-data get-app-data)


(defn numberize-calendar-response
  [calres]
  {:day (utils/integerize-keyword-keys (:day calres))
   :month (utils/integerize-keyword-keys (:month calres))
   :year (utils/integerize-keyword-keys (:year calres))})

(defn got-app-data
  "TESTED"
  [db [_ response]]
  (->
   (meta-db/set-loading db false)
   (assoc :calendar-can-select (numberize-calendar-response (:calendar-can-select response)))
   (assoc :date-now (:date-now response))
   (assoc :calendar (numberize-calendar-response (:calendar response)))
   (assoc :cloud (:cloud response))
   (assoc :total-nodes (:total-nodes response))
   (assoc :cloud-can-select (:cloud-can-select response))
   (assoc :tree-tag (:tree-tag response))))
(refe/reg-event-db :cloud/got-app-data got-app-data)


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
  [{:keys [db]} [_ group item]]
  {:db
   (if (=  group "FastAccess")
     (click-on-fast-access-item db group item)
     (try
       (let [already-has-item (get-in db [:calendar-selected group])]
         (cond
           (and  item (> item 0) (= already-has-item item))
           (update db :calendar-selected dissoc group)
           (and  item (> item 0))
           (assoc-in db [:calendar-selected group] item)
           :else
           db))
       (catch :default e
         db)))
   :fx-redirect [:cloud/get-app-data]})


(refe/reg-event-fx :cloud/click-on-calendar-item click-on-calendar-item)

(defn discard-selection
  [db]
  (assoc db :cloud-selected #{}
         :calendar-selected {}))
(refe/reg-event-db :cloud/discard-selection discard-selection)

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
        cloud-selected))
    :cloud-can-select {})
   :fx-redirect [:cloud/get-app-data]})

(refe/reg-event-fx :cloud/clicked-cloud-item click-on-cloud)

(defn clicked-many-cloud-items
  "TESTED"
  [{:keys [db]} [_ items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     :else
     (assoc db :cloud-selected (into #{} items)))
   :fx-redirect [:cloud/get-app-data]})
(refe/reg-event-fx :cloud/clicked-many-cloud-items  clicked-many-cloud-items)

(defn breadcrumbs-show-all?-switch
  [db _]
  (update db :breadcrumbs-show-all? not))
(refe/reg-event-db :cloud/breadcrumbs-show-all?-switch  breadcrumbs-show-all?-switch)


(defn click-on-disabled-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (if (keyword? item)
     (assoc  (discard-selection db)
             :cloud-selected #{item}
             :cloud-can-select {})
     db)
   :fx-redirect [:cloud/get-app-data]})

(refe/reg-event-fx :cloud/clicked-disabled-cloud-item click-on-disabled-cloud)

(defn click-on-disabled-calendar
  "TESTED"
  [{:keys [db]} [_ group item]]
  {:db
   (:db (click-on-calendar-item {:db (discard-selection db)} [nil group item]))
   :fx-redirect [:cloud/get-app-data]})

(refe/reg-event-fx :cloud/clicked-disabled-calendar-item click-on-disabled-calendar)
