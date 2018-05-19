(ns miktau.generic.effects
  (:require
   [day8.re-frame.http-fx]
   [re-frame.core :as refe]))

(refe/reg-fx
 :log!
 (fn [data]
   (.log js/console (str data))))

(refe/reg-fx
 :fx-redirect
 (fn [data]
   (refe/dispatch data)))
