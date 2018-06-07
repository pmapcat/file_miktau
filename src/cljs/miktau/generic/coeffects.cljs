(ns miktau.generic.coeffects
  (:require
   [day8.re-frame.http-fx]
   [miktau.tools :as utils]
   [re-frame.core :as refe]))

(refe/reg-cofx
 :generic/local-store
 (fn [coeffects local-store-key]
   (assoc coeffects
          local-store-key (utils/ls-get local-store-key))))
