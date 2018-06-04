(ns miktau.breadcrumbs.events
  (:require [miktau.tools :as utils]
            [re-frame.core :as refe]))

(defn clear
  "TESTED"
  [{:keys [db]}  [_ redirector]]
  {:db 
   (assoc db :cloud-selected #{}
          :meta-cloud-selected #{})
   :fx-redirect redirector})
(refe/reg-event-fx :breadcrumbs/clear clear)

(defn breadcrumbs-show-all?-switch
  [db _]
  (update db :breadcrumbs-show-all? not))
(refe/reg-event-db :breadcrumbs/breadcrumbs-show-all?-switch  breadcrumbs-show-all?-switch)


(defn click-on-cloud
  "TESTED"
  [{:keys [db]} [_ redirector item]]
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
   :fx-redirect redirector})
(refe/reg-event-fx :breadcrumbs/clicked-cloud-item click-on-cloud)


(defn clicked-many-cloud-items
  "TESTED"
  [{:keys [db]} [_ redirector items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     :else
     (assoc db :cloud-selected (into #{} items)))
   :fx-redirect redirector})
(refe/reg-event-fx :breadcrumbs/clicked-many-cloud-items  clicked-many-cloud-items)

(defn clicked-many-meta-cloud-items
  "TESTED"
  [{:keys [db]} [_ redirector items]]
  {:db
   (cond
     (not (utils/seq-of-predicate? items keyword?)) db
     :else
     (assoc db :meta-cloud-selected (into #{} items)))
   :fx-redirect redirector})
(refe/reg-event-fx :breadcrumbs/clicked-many-meta-cloud-items  clicked-many-meta-cloud-items)

