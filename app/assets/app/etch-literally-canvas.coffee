class EtchCanvasLink
  constructor: (@scope, @element) ->
    @control = @scope.control
    @control.getImageBase64 = @getImageBase64
    @control.setSrc = @setSrc

    @element.literallycanvas
      imageURLPrefix: '/assets/lib/literallycanvas/literallycanvas-0.3-rc4/img'
      onInit: (literallyCanvas) =>
        @literallyCanvas = literallyCanvas
        @canvas = literallyCanvas.canvasForExport()
        @control.ready()


  getImageBase64: =>
    @canvas.toDataURL()

  setSrc: (base64Image) =>
    image = new Image()
    image.src = base64Image
    @literallyCanvas.saveShape new LC.ImageShape(0, 0, image)



define ['app/module'], (module) ->
  module.directive "etchLiterallyCanvas", ->
    restrict: "AE"
    scope:
      'control': '=control'
    link: (scope, element) ->
      new EtchCanvasLink scope, element
