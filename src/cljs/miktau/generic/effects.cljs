(ns miktau.generic.effects
  (:require
   [day8.re-frame.http-fx]
   [day8.re-frame.undo :as undo]
   [miktau.tools :as utils]
   [re-frame.core :as refe]))

(refe/reg-fx
 :log!
 (fn [data]
   (do
     (.log js/console (str data))
     (refe/dispatch [:ui-log/register-error (str data)]))))

(refe/reg-fx
 :purge-undo-history!
 (fn [_]
   (undo/clear-history!)))

(refe/reg-fx
 :generic/set-local-store!
 (fn [[key val]]
   (utils/ls-set! key val)))

(refe/reg-fx
 :defer-fx-redirect
 (fn [data]
   (utils/set-timeout!
    #(refe/dispatch (into [] (rest data)))
    (first data))))


(refe/reg-fx
 :fx-redirect
 (fn [data]
   (refe/dispatch data)))


