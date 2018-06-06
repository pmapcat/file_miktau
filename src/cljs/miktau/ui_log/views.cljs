(ns miktau.ui-log.views
  (:require [re-frame.core :as refe]))

(defn main
  []
  (let [err @(refe/subscribe [:ui-log/error])]
    (if (:display? err)
      [:div.padded-as-button {:style {:position "fixed" :right "0" :bottom "0"
                                      :margin "30px"
                                      :padding "20px"
                                      :box-shadow "0px 2px 3px 0px grey"
                                      :background "white"}}
       (:message err)]
      [:span])))

