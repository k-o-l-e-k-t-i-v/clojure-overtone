;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(def pattern-1 (atom [ [:bd 1]
                       [:bd 1]
                       [:sd 1]
                       [:sd 1]
                       [:hh 1]
                       [:hh 1]
                       [:hh 1]]))

(println @pattern-1)
(first @pattern-1)

(swap! pattern-1 rotate 1)

(odoc swap!)

(println (rotate 1 pattern-1))

(println pattern-1)

(reset! pattern-1 [[:db 1]
                   [:sd 2]])




(def tik 1000)

(defn tiker [n t p]
  (let [x (first p)
        x1 (first x)
        x2 (second x)
        ]
    (at n (cond (= x1 :sd) (baduk)
                (= x1 :bd) (baduk :noise-amt 10 :noise-freq 200)
                (= x1 :hh) (baduk :noise-amt 100 :noise-freq 2000)))
    (apply-at (+ n (* x2 t)) tiker (+ n t) t (rotate 1 p) [])))

(tiker (now) (* 1.5 tik) pattern-1)

(stop)

(definst baduk [noise-amt 2 noise-freq 4]
  (let [x (env-gen (perc 0 0.5 -10) 1 1 0 1 FREE)
        x2 (env-gen (perc 0 0.05 -10) 1 1 0 1)
        s (sin-osc (+ 40 (* 200 x2 (* noise-amt (lf-noise2:ar noise-freq)))))
        a (* s x)
        a (freq-shift a -70)]
    (pan2 a)))

(baduk)
