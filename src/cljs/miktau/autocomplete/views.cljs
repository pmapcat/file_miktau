(ns miktau.autocomplete.views
  (:require [miktau.autocomplete.autocomplete :as autocomplete-widget]
            [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]))

(defn filter-input [get-app-data-cb can-enter-new?]
  (let [completions @(refe/subscribe [:autocomplete/cloud-with-context])]
    [:div.pure-g.padded-as-button
     [:div.pure-u-7-8 
      [autocomplete-widget/autocomplete-widget
       (map name (keys completions))
       {:submit-fn (fn [data] (refe/dispatch [:autocomplete/clear-cloud-click get-app-data-cb (keyword (str data))]))
        :can-enter-new? can-enter-new?
        :render-fn (fn [cur-input selected? text]
                     [:div.unstyled-link.complete {:class  (if  selected?  " complete selected " "")
                                                   :style {:padding "10px"}}
                      [:span {:style {:color "green"}}
                       cur-input]
                      (apply str (drop (count cur-input) text))
                      [:span {:style {:padding-left "10px" :font-weight "300"}}
                       "[ "
                       (for [item (take 5 ((keyword text) completions ))]
                         ^{:key item}
                         [:span (name item) " "])
                       "]"]])}]]
     [:div.pure-u-1-8.mik-flush-right
      [:div.pure-button.pure-button-primary  {:style {:width "80%"}}
       [views-utils/icon "search"]]]]))
