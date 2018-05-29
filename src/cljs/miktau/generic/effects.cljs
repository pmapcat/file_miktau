(ns miktau.generic.effects
  (:require
   [day8.re-frame.http-fx]
   [day8.re-frame.undo :as undo]
   [re-frame.core :as refe]))

(refe/reg-fx
 :log!
 (fn [data]
   (.log js/console (str data))))

(refe/reg-fx
 :purge-undo-history!
 (fn [_]
   (undo/clear-history!)))

(refe/reg-fx
 :fx-redirect
 (fn [data]
   (refe/dispatch data)))


