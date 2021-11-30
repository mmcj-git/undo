# Undo Drawing App
Undo is an Android drawing app made specifically for people with bad eye sight so that is accessible for screen readers.
This project was used to explore Android Studio as well as HCI concepts such as the 10 usablity heurisitics and the gulf of evaluation vs the gulf of execusion. 

## Main Usability Features:
#

  Alt Text for screen readers, recognizable images to accompany buttons, and high     contrast text
  
  [screenshot of buttons that are easy to read]

### Color wheel
  The color wheel snaps back to the last position it was on when the user's finger moves off the wheel
  
  ![color picker](https://user-images.githubusercontent.com/64673976/144129229-683f7271-e5cb-4ea3-b774-b53af0679600.gif)
  
  This usablity feature was one of the hardest to implement because it involved a couple different coding concepts that were foreign to me before this project. The first is PPS.
  
  <img width="688" alt="PPS" src="https://user-images.githubusercontent.com/64673976/144126863-92b421e6-2f57-416a-88aa-2ee77144e4a4.PNG">

  This is a simplified PPS diagram of the color wheel and how it should function. My job as the implementer was to make sure that the model updated whenever the user moved into a new phase of the diagram so that the end state would be correct. The other thing that I would have to account for 

### Undo and redo features along with an eraser to help correct errors
  
  [gif using undo/redo]
