# Undo Drawing App
Undo is an Android drawing app made specifically for people with bad eye sight so that is accessible for screen readers.
This project was used to explore Android Studio as well as HCI concepts such as the 10 usablity heurisitics and the gulf of evaluation vs the gulf of execusion. 

## Main Usability Features:
### Easy buttons
  
<img width="194" alt="buttons" src="https://user-images.githubusercontent.com/64673976/144129396-aa744f09-15c4-40aa-af0b-9c743f37c65e.PNG">

    For the buttons I made sure to include alt text for scrren readers, vibrant colors to make them easy to see, as well as high contrast text and recognizable images for each function. The buttons also greyed out and were unclickable when the color wheel was being used, which is shown in the gifs below.

### Color wheel
  The color wheel snaps back to the last position it was on when the user's finger moves off the wheel.
  
  ![color picker](https://user-images.githubusercontent.com/64673976/144129229-683f7271-e5cb-4ea3-b774-b53af0679600.gif)
  
  This usablity feature was one of the hardest to implement because it involved a couple different coding concepts that were foreign to me before this project. The first is PPS.
  
  <img width="688" alt="PPS" src="https://user-images.githubusercontent.com/64673976/144126863-92b421e6-2f57-416a-88aa-2ee77144e4a4.PNG">

  This is a simplified PPS diagram of the color wheel and how it should function. My job as the implementer was to make sure that the model updated whenever the user moved into a new phase of the diagram so that the end state would be correct. The other thing that I would have to account for was unexpected user behavior. That is why this PPS is set up the way it is, the mouse is either pressed or released (start and end states) and it is either inside or outside the wheel. This leads us into the next coding concepts I learned about in this project, essential geometry.
  
  To be able to know if the mouse in inside or outside the wheel I needed to use essential geometry to calculate where exactly the color wheel was. Since different phones have different size screens, the color wheel needed to be able to grow and shrink to accomodate that. Therefore the location of the color wheel changed and whether or not a certain pixel coordinate was inside or outside the wheel changed as well. Even when that was calculated and the mouse was determined to be inside the wheel I needed to calculate exactly what color it was on. This calculation was based on the angle from the center of the wheel and determined what color was used when the user released the mouse and where the selecting circle (the smaller circle that shows what color you have selected) is. 
  
  I also worked with event listeners to change the color of the brush once a color is selected.

### Undo and redo features along with an eraser to help correct errors
  
  [gif using undo/redo]
