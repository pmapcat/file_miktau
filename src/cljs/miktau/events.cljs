(ns miktau.events
  (:require
   [miktau.utils :as utils]
   [re-frame.core :as refe]
   [clojure.set :as clojure-set]
   [miktau.query-building :as query-building]))

(def init-db
  {:loading? true})

(refe/reg-event-fx
 :init
 (fn [_ _]
   {:db (assoc init-db :loading? true :nodes-selected #{} :cloud-selected #{})
    :fx-redirect [:get-app-data]}))
