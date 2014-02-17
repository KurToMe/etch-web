class EtchCanvasLink
  constructor: (@scope, @element) ->
    @ctx = @element[0].getContext("2d")
    # variable that decides if something should be drawn on mousemove
    @drawing = false
    # the last coordinates before the current move
    @lastX = undefined
    @lastY = undefined

    @element.bind "mouseup", @onMouseUp
    @element.bind "mousedown", @onMouseDown
    @element.bind "mousemove", @onMouseMove

  reset: =>
    # canvas reset
    @element[0].width = @element[0].width

  draw: (lX, lY, cX, cY) ->
    # line from
    @ctx.moveTo lX, lY
    # to
    @ctx.lineTo cX, cY
    # color
    @ctx.strokeStyle = "#4bf"
    # draw it
    @ctx.stroke()

  # begins new line
  onMouseDown: (event) =>
    @lastX = event.offsetX
    @lastY = event.offsetY
    @ctx.beginPath()
    @drawing = true

  onMouseMove: (event) =>
    if @drawing
      # get current mouse position
      currentX = event.offsetX
      currentY = event.offsetY
      @draw @lastX, @lastY, currentX, currentY
      # set current coordinates to last one
      @lastX = currentX
      @lastY = currentY

  onMouseUp: (event) =>
    # stop drawing
    @drawing = false


define ['app/module'], (module) ->
  module.directive "etchCanvas", ->
    restrict: "AE"
    scope:
      'control': '=control'
    link: (scope, element) ->
      new EtchCanvasLink scope, element
