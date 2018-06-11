(ns miktau.autocomplete.views
  (:require [miktau.autocomplete.autocomplete :as autocomplete-widget]
            [reagent.core :as reagent]
            [re-frame.core :as refe]))

(defn filter-input-no-btn
  ([get-app-data-cb can-enter-new?]
   (filter-input-no-btn get-app-data-cb can-enter-new? {}))
  ([get-app-data-cb can-enter-new? options]
   (let [completions @(refe/subscribe [:autocomplete/cloud-with-context])]
     [autocomplete-widget/autocomplete-widget
      (map name (keys completions))
      (merge
       {:can-enter-new? can-enter-new?
        :placeholder
        "Enter something here"
        :submit-fn
        (fn [data] (refe/dispatch [:autocomplete/clear-cloud-click get-app-data-cb (keyword (str data))]))
        
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
                       "]"]])} options)])))

(defn filter-input
  ([get-app-data-cb can-enter-new?]
   (filter-input
    get-app-data-cb can-enter-new? {}))
  ([get-app-data-cb can-enter-new? options]
   (let [completions @(refe/subscribe [:autocomplete/cloud-with-context])
         app-state   (reagent/atom {:cur-index 0 :cur-input ""})]
     [:div.pure-g.padded-as-button
      [:div.pure-u-7-8
       [autocomplete-widget/autocomplete-widget
        (map name (keys completions))
        (merge
         {:can-enter-new? can-enter-new?
          :placeholder
          "Enter something here"
          :submit-fn
          (fn [data] (refe/dispatch [:autocomplete/clear-cloud-click get-app-data-cb (keyword (str data))]))
          :app-state  app-state
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
                         "]"]])} options)]]
      [:div.pure-u-1-8.mik-flush-right
       [:div.pure-button.pure-button-primary  {:style {:width "80%"}}
        [:div {:on-click #(refe/dispatch ((:submit-fn options) (:cur-input @app-state)))} "Find"]]]])))
