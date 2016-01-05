(ns mystic.components
  (:require
   [rum.core :as rum]
   ))

(def mystic-r 190)
(def mystic-o {:x 200 :y 200})

(defn dot-coord [key theta]
  (+ (key mystic-o) (* mystic-r ((if (= key :x) Math.cos Math.sin) theta))))

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


(def mystic-n 50)

(rum/defc mystic-rose []
  [:div
   (start-button #(prn "start"))
   (reset-button #(prn "reset"))
   [:input.inp-default {:type "number" :min 5 :max 100}]
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     [:g
      (map-indexed #(rum/with-key (dot (/ (* 2 Math.PI %1) mystic-n)) %2) (range mystic-n))]
     ]]])
