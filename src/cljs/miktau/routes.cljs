(ns miktau.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]))

;; (def state (atom {}))

;; (def app-routes
;;   ["/" {"foo" :foo}])

;; (defn set-page! [match]
;;   (swap! state assoc :page match))

;; (comment
;;   (bidi/match-route app-routes "/foo"))

;; (def history
;;   (pushy/pushy set-page! (partial bidi/match-route app-routes)))

;; (pushy/start! history)

