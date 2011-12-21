/**
 * Generated by Gas3 v2.2.0 (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERWRITTEN EACH TIME YOU USE
 * THE GENERATOR. INSTEAD, EDIT THE INHERITED CLASS (DistanceCheckImpl.as).
 */

package org.openforis.idm.metamodel.impl {

    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    import org.openforis.idm.metamodel.DistanceCheck;

    [Bindable]
    public class DistanceCheckImplBase extends AbstractCheck implements DistanceCheck {

        private var _destinationPointExpression:String;
        private var _maxDistanceExpression:String;
        private var _minDistanceExpression:String;
        private var _sourcePointExpression:String;

        [Bindable(event="unused")]
        public function get destinationPointExpression():String {
            return _destinationPointExpression;
        }

        [Bindable(event="unused")]
        public function get maxDistanceExpression():String {
            return _maxDistanceExpression;
        }

        [Bindable(event="unused")]
        public function get minDistanceExpression():String {
            return _minDistanceExpression;
        }

        [Bindable(event="unused")]
        public function get sourcePointExpression():String {
            return _sourcePointExpression;
        }

        override public function readExternal(input:IDataInput):void {
            super.readExternal(input);
            _destinationPointExpression = input.readObject() as String;
            _maxDistanceExpression = input.readObject() as String;
            _minDistanceExpression = input.readObject() as String;
            _sourcePointExpression = input.readObject() as String;
        }

        override public function writeExternal(output:IDataOutput):void {
            super.writeExternal(output);
            output.writeObject(_destinationPointExpression);
            output.writeObject(_maxDistanceExpression);
            output.writeObject(_minDistanceExpression);
            output.writeObject(_sourcePointExpression);
        }
    }
}