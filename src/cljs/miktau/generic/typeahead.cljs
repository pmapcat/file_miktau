(ns miktau.generic.typeahead
  (:require [reagent.core :as reagent]
            [clojure.string :as s]))

(def states
  ["Alabama" "Alaska" "Arizona" "Arkansas" "California"
   "Colorado" "Connecticut" "Delaware" "Florida" "Georgia" "Hawaii"
   "Idaho" "Illinois" "Indiana" "Iowa" "Kansas" "Kentucky" "Louisiana"
   "Maine" "Maryland" "Massachusetts" "Michigan" "Minnesota"
   "Mississippi" "Missouri" "Montana" "Nebraska" "Nevada" "New Hampshire"
   "New Jersey" "New Mexico" "New York" "North Carolina" "North Dakota"
   "Ohio" "Oklahoma" "Oregon" "Pennsylvania" "Rhode Island"
   "South Carolina" "South Dakota" "Tennessee" "Texas" "Utah" "Vermont"
   "Virginia" "Washington" "West Virginia" "Wisconsin" "Wyoming"])

(defn matcher [strs]
  (fn [text callback]
    (->> strs
         (filter #(s/includes? % text))
         (clj->js)
         (callback))))

(defn typeahead []
  (let [typeahead-value (reagent/atom nil)]
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        (.typeahead (js/$ (reagent/dom-node this))
                    (clj->js {:hint true
                              :highlight true
                              :minLength 1})
                    (clj->js {:name "states"
                              :source (matcher states)})))
      :reagent-render
      (fn []
        [:input.typeahead
         {:type :text
          :on-select #(reset! typeahead-value (-> % .-target .-value))
          :placeholder "States of USA"}])})))
