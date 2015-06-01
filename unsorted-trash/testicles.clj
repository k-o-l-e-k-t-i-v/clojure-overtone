;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(defsynth x [ freq 30 dens 1 t 30 feed 3]
  (let [x (decay2 (dust dens) 0.002 0.16)
        env (env-gen:ar (envelope [0 1 0] [t t]) 1 1 0 1 FREE)]
    (out [0 1 ]
         (free-verb

          (* env (* 8 (rlpf (sin-osc-fb (+ (* 100 x) freq) feed) (* 0.5 freq) 0.03)) x)
          0.9 0.97 0.2
          )

                  )))
(x)
(defn rwall [n f f2 t t2]
  (dotimes [nx n]
    (let [r (+ f (rand f2))
          r2 (+ 1 (rand 6))
          r3 (+ t (rand t2))
          f (rand 5)]
      (x r r2 r3 f))))


(rwall 10 (midi->hz (note :c5)) 10)
(rwall 20 (midi->hz (note :c4)) 10 20 0)
(defn jmtof [x] (midi->hz x))
(x 1432 4 30)
(x 59 4)
(x 110)
(x 1100 4)
(group "source" :head)
(group "efx" :tail)

(definst kick [freq 120  dur 0.3 width 0.5]
  (let [freq-env (* freq (perc 0 (* 0.29 dur) 1 -5))
        env (env-gen (perc 0.01 dur) 1 1 0 1 FREE)
        sqr (* (env-gen (perc 0 0.01)) (pulse (* 0.2 freq) width))
        src (sin-osc freq-env )
        drum (+ sqr (* env src))]
    (compander drum drum 0.2 1 0.1 0.01 0.01)))

(kick :freq 200)





(node-tree)
(synthdef "x" [ freq 30 dens 1 t 30 feed 3]
  (let [x (decay2 (dust dens) 0.002 0.16)
        env (env-gen:ar (envelope [0 1 0] [t t]) 1 1 0 1 FREE)]
    (out [0 1 ]
         (free-verb
          (* env (* 8 (rlpf (sin-osc-fb (+ (* 100 x) freq) feed) (* 0.5 freq) 0.03)) x)
          0.9 0.97 0.2
          ))))

(stop)

(defsynth rev []
  (out [0 1] (free-verb (in 0 1) 0.9 0.97 0.7)))
(rev)

(node "x" {} {:target 0})

(stop)



(definst sil [freq 40 dur 0.2 shift 0]
  (let [amp-env (line:kr 1 0 dur)
        freq-env (line:kr (* 10 freq) freq (* 0.2 dur))
        sig (sin-osc freq-env)
        drum (resonz (* amp-env sig) freq-env 1.28)
        verb (freq-shift (free-verb drum 0.37 0.89 0.2) shift 0.2)
        silencio (detect-silence verb 0.001 0.2 FREE)
        ]
    (out [0 1] verb)))

(sil 20 0.3)

(def m1 (metronome 180))
(m1)
(def mel [:c1 :f1 :g1 ])
(def mel2 [:d#1 :g#1 :f1 ])
(midi->hz (note (rand-nth mel)))
(defn beat2 [b ]
  (at (m1 b) (let []
               (sil (midi->hz (note (rand-nth mel2))) (rand 0.3) (* 10 (midi->hz (note (rand-nth mel)))))))
 ; (apply-by (m1 (inc b)) #'beat (+ 1 (* 3 (rand-int 3)) b)  [])
  )

(beat (m1))
(beat2 (m1))

(beat (m1) (midi->hz (note :c3)))
(node-tree)
(stop)
(clear-all)
(clear-instruments)
