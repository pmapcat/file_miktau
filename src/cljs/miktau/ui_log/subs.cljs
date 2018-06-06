(ns miktau.ui-log.subs
  (:require [re-frame.core :as refe]))

(refe/reg-sub
 :ui-log/error
 (fn [db _ ]
   (let [msg (or  (:ui-log-error db) "")]
     {:message  msg
      :display? (not (empty? msg))})))


