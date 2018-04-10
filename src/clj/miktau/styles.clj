(ns miktau.styles)
;; ============================  >  GENERAL  <  =========================== 
(defn- mik-cut
  [on]
  [(str ".mik-cut-" on)  { (str "padding-" on) "0"
                          (str "margin-" on) "0"}])

(defn- mik-rotate [rad]
  [(str ".mik-rotate-" rad)
   {:-webkit-transform (str "rotate(" rad "deg)")
    :-moz-transform (str "rotate(" rad "deg)")
    :-ms-transform (str "rotate(" rad "deg)")
    :-o-transform (str "rotate(" rad "deg)")
    :transform (str "rotate(" rad "deg)")}])

(defn- mik-flush
  [on]
  [(str ".mik-flush-" on) {:text-align on}])
(defn- mik-float
  [on]
  [(str ".mik-float-" on) {:float on}])

(defn general-classes []
  [(map mik-cut   ["top" "bottom" "left" "right"])
   (map mik-float ["left" "right"])
   (map mik-flush ["left" "right" "center"])
   (map mik-rotate [45 90 180])
   [:.padded-as-button {:padding-top 12.8
                        :padding-bottom 6.4
                        :padding-left 12.8
                        :padding-right 12.8}]
   [:.margin-as-button {:margin-top 12.8
                        :margin-bottom 6.4
                        :margin-left 12.8
                        :margin-right 12.8}]])

;; ============================  >  TYPE  <  ===========================

(def type-link "https://fonts.googleapis.com/css?family=Signika|Cardo|Source+Sans+Pro")
(def fonts
  {:header     "\"Signika\", sans-serif !important"
   :body        "\"Source Sans Pro\", sans-serif !important"
   :tags        "\"Cardo\", serif !important"})

;; ============================  > COLOR  <  ===========================
;; go to css-utils to understand how this works
(def colors  {:prima {-2 "#3C8D2F", -1 "#A6ABA5", 0 "#6D9C65", 1 "#137D02", 2 "#0F6E00"}, :seconda {-2 "#AA8939", -1 "#CFCDC8", 0 "#BCA97A", 1 "#986D03", 2 "#855F00"}, :terza {-2 "#4B2D73", -1 "#8A888C", 0 "#685780", 1 "#320A67", 2 "#27015B"}})
(defn co [& args]
  (get-in colors args))

;; =====================================================================
(def screen
  [
   ;; tables 
   [:.minimal-table {:width "100%" :border "none !important"}
    [:&>thead {:background "none !important" :border-bottom "1px"}
     [:&>tr {:border-bottom "solid 2px;"}]]
    
    [:&>tbody
     [:&>tr:hover {:background "#dddddd" :color "white" :cursor "pointer"}]
     [:&>tr>td {:padding-top "0.5em"}]]
    
    [:& :td :th {:border "none !important" :padding "0 !important"}]]
   ;; tag input
   [[:tags :input {:background "none !important"}]
    [:tags :tag :x:hover {:background "#ABABA9"}]
    [:tags :tag>div:before {:background "#dddddd"}]]
   [:.table-hover:hover {:background "white !important"}]
   
   ;; dropdowns 
   [:.dropdown
    {:position "relative" :display "inline-block"
     :font-weight "600"
     :cursor "pointer"}
    [:&:after
     {:content "\" \\25BE\""
      :color "#a7a6a6"
      :font-size "0.8em;"}]
    [:&-content
     {:display "none" :position "absolute" :background "#f7f7f7 !important"
      :z-index "999"}]
    [:&:hover>.dropdown-content {:display "block"}]
    [:&:hover {:color "#4d4d4d"}]]
   [:.pure-g {:display "flex"}]
   
   [:.background-0 {:background "#ffffff"}]
   [:.background-1 {:background "#f7f7f7" }]
   [:.background-2 {:background "#f1f1f1"}]
   [:.background-3 {:background "#ebebeb"}]
   [:.background-4 {:background "#e7e7e7"}]
   
   [:.unstyled-link {:text-decoration "none" :font-weight "900" :color "#000000"}]
   [:.unstyled-link:hover {:text-decoration "none" :color "#ababab"}]
   
   [:.shadow {:box-shadow 'none}]
   [:.pure-menu-link {:color "#000000 !important" :background "none !important"}]
   [:.pure-menu-link:hover {:color "#4d4d4d !important" :background "#ababab !important"}]
   [:.pure-button       {:background "none !important" :font-weight "600 !important" :font-family (:body fonts)}]
   [:.pure-button:hover {:color "#4d4d4d !important"}]
   [:.gray   {:color "#ababab"}]
   [:.black  {:color "#000000"}]
   [:.orange {:color "#4d4d4d"}]
   [:.light-gray  {:color "#dddddd"}]
   [:.header-font  {:font-family (:body fonts)}]
   [:.body-font {:font-family (:body fonts)}]
   [:.tag    {:font-family (:body fonts)}]
   [:.tag:hover {:color "#4d4d4d !important"} ]
   (general-classes)])
