(ns ^:figwheel-always mystic.components
  (:require
   [rum.core :as rum]
   [mystic.core :as core :refer [model]]
   ))

(def min-nodes 1)
(def max-nodes 50)
(def mystic-r 190)
(def mystic-o {:x 200 :y 200})

(defn mean [a b]
  (/ (+ a b) 2))

(defn dot-coord [key theta]
  (+ (key mystic-o) (* mystic-r ((if (= key :x) Math.cos Math.sin) theta))))

(defn i->theta [n i]
  (/ (* 2 Math.PI i) n))

(defn thetas [n]
  (map #(i->theta n %) (range n)))

(rum/defc start-button [handler]
  [:button.start.btn-default.btn-left
   {:on-click handler
    :on-touch-start handler} "Start"])

(rum/defc reset-button [handler]
  [:button.start.btn-default.btn-right
   {:on-click handler
    :on-touch-start handler} "Reset"])

(rum/defc dot [theta]
  [:circle {:stroke "#ffffff" :stoke-width 5 :fill "#CCCCCC" :r 4
            :cx (dot-coord :x theta)
            :cy (dot-coord :y theta)}])

#_(rum/defc count-input < rum/cursored [mystic-n]
  [:span.node-count
   [:input.inp-default
    {:type "number"
     :min min-nodes :max max-nodes :value @mystic-n
     :on-change #(swap! core/model assoc :mystic-n (.parseInt js/Number (-> % .-target .-value)))}]
   "points"])


(rum/defc n-slider < rum/cursored [mystic-n]
  [:input {:type "range" :value @mystic-n :min min-nodes :max max-nodes
           :style {:width "100%"}
           :on-change #(swap! core/model assoc
                              :mystic-n (-> % .-target .-value))}])


(rum/defc count-input < rum/cursored [mystic-n]
  [:span.node-count
   [:span
    [:input.inp-default
     {:type "number"
      :min min-nodes :max max-nodes :value @mystic-n
      :on-change #(swap! core/model assoc :mystic-n (.parseInt js/Number (-> % .-target .-value)))}]
    "points"]
   (n-slider (rum/cursor model [:mystic-n]))])


(defn slider [param value min max]
  (let [reset (case param :mystic-n :mystic-t)]
    [:input {:type "range" :value value :min min :max max
             :style {:width "100%"}
             :on-change #(swap! core/model assoc
                                param (-> % .-target .-value))}]))


(rum/defc dots-on-circle < rum/cursored [mystic-n]
  [:g
   (map-indexed  #(rum/with-key (dot %2) %1) (thetas @mystic-n))])

(rum/defc semi-chord [theta1 theta2]
  [:line {:x1 (dot-coord :x theta1)
          :y1 (dot-coord :y theta1)
          :x2 (mean (dot-coord :x theta1) (dot-coord :x theta2))
          :y2 (mean (dot-coord :y theta1) (dot-coord :y theta2))
          :stroke "rgba(0,0,0,0.2)"
          :stroke-width 1
          }])

(rum/defc chords [mystic-n]
  (let [mystic-thetas (thetas @mystic-n)]
    [:g
     (for [theta1 mystic-thetas
           theta2 mystic-thetas]
       (semi-chord theta1 theta2))])
  )

(rum/defc select-method < rum/cursored [method]
  [:select
   [:option 1]
   [:option 2]
   ])


(rum/defc mystic-rose < rum/cursored rum/cursored-watch [model]
  [:div
   #_(start-button #(prn "start"))
   #_(reset-button #(prn "reset"))
   (select-method (rum/cursor model [:method]))
   (count-input (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]))
     ]]
])
