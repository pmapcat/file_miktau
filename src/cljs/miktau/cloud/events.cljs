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

(defn clear
  "TESTED"
  [{:keys [db]}  _]
  {:db 
   (assoc db
       :filtering ""
       :cloud-selected #{})
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
  {:db db ;; (meta-db/set-loading db true)
   :http-xhrio
   (utils/server-call
    {:url "/api/get-app-data"
     :params
     {:modified (or (:calendar-selected db) {})
      :sorted   ""
      :tags     (or (into [] (sort (map str (map name (:cloud-selected db))))) [])}}
    :cloud/got-app-data :http-error)})

(refe/reg-event-fx :cloud/get-app-data get-app-data)

(defn got-app-data
  "TESTED"
  [db [_ response]]
  (let [got-app-data-if-diff
        (fn [db key]
          (if-not (= (key db) (key response))
            (assoc db key  (key response))
            db))]
    (->
     (meta-db/set-loading db false)
     (got-app-data-if-diff :calendar-can-select)
     (got-app-data-if-diff :date-now)
     (got-app-data-if-diff :calendar)
     (got-app-data-if-diff :cloud)
     (got-app-data-if-diff :cloud-can-select)
     (got-app-data-if-diff :tree-tag))))
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
        cloud-selected)))
   :fx-redirect [:cloud/get-app-data]})

(refe/reg-event-fx :cloud/clicked-cloud-item click-on-cloud)

(defn click-on-disabled-cloud
  "TESTED"
  [{:keys [db]} [_ item]]
  {:db
   (if (keyword? item)
     (assoc  (discard-selection db) :cloud-selected #{item})
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

