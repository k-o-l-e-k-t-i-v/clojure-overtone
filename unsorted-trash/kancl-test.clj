;;
;;     MM""""""""`M
;;     MM  mmmmmmmM
;;     M`      MMMM 88d8b.d8b. .d8888b. .d8888b. .d8888b.
;;     MM  MMMMMMMM 88''88'`88 88'  `88 88'  `"" Y8ooooo.
;;     MM  MMMMMMMM 88  88  88 88.  .88 88.  ...       88
;;     MM        .M dP  dP  dP `88888P8 '88888P' '88888P'
;;     MMMMMMMMMMMM
;;
;;         M""MMMMMMMM M""M M""MMMMM""M MM""""""""`M
;;         M  MMMMMMMM M  M M  MMMMM  M MM  mmmmmmmM
;;         M  MMMMMMMM M  M M  MMMMP  M M`      MMMM
;;         M  MMMMMMMM M  M M  MMMM' .M MM  MMMMMMMM
;;         M  MMMMMMMM M  M M  MMP' .MM MM  MMMMMMMM
;;         M         M M  M M     .dMMM MM        .M
;;         MMMMMMMMMMM MMMM MMMMMMMMMMM MMMMMMMMMMMM  Version 1.0beta25
;;
;;           http://github.com/overtone/emacs-live
;;
;; Hello Jr, it's lovely to see you again. I do hope that you're well.

(definst sawka [freq 440 detune 1 amp 0.3 a 0.61 d 0.81 s 0.5 r 0.6 acurve 0 t 1]
  (* (env-gen:ar (envelope [0 1 s s 0] [a d t r] acurve) 1 1 0 1 FREE)
     (rlpf (saw (* freq detune)) (* 0.5 freq) 0.15)))


(demo 2 (sawka :freq 120 :s 0.1 :t 0.2))
(sawka :freq 80 :amp 0.96 :s 0.96 :t 2)
(stop)

(def metro-1 (metronome 320))
(metro-1)
(metro-bpm metro-1 800)
(metro-beat metro-1)
(def bimbas (sawka :freq 180 :amp 2.7 :t 0.03 :a 0.01 :r 0.13 :d 0.01 :s 2))

(defn t1 [x beat-num t]
  (at (x (+ 0 beat-num)) (sawka :freq 80 :amp 0.96 :t (* 0.5 t)))
  (apply-at (x (+ t beat-num)) t1 x (+ t beat-num) t []))

(defn t2 [ beat-num t f]
  (at (metro-1 (+ 0 beat-num))
      (sawka :freq (* (+ 1 (rand-int 6)) f) :amp 0.96 :t (* 0.25 t) :a 0.01 :d 0.2 :r 0.2))
  (apply-at (metro-1 (+ t beat-num)) t2 (+ t beat-num) t f [])
  )

(t1 metro-1 (metro-1) 8)
(t2 (metro-1) 9 200)
(t2 (metro-1) 5 220)
;(stop 548)
(t2 (metro-1) 3 150)
(stop)
