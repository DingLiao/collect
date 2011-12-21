/**
 * Generated by Gas3 v2.2.0 (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERWRITTEN EACH TIME YOU USE
 * THE GENERATOR. INSTEAD, EDIT THE INHERITED CLASS (CodeListItemImpl.as).
 */

package org.openforis.idm.metamodel.impl {

    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    import mx.collections.ListCollectionView;
    import org.openforis.idm.metamodel.CodeListItem;

    [Bindable]
    public class CodeListItemImplBase extends AbstractVersionable implements CodeListItem {

        private var _children:ListCollectionView;
        private var _codeDefinitions:ListCollectionView;
        protected var _deprecatedAttribute:String;
        private var _descriptions:ListCollectionView;
        private var _labels:ListCollectionView;
        private var _qualifiable:Boolean;
        protected var _sinceAttribute:String;

        [Bindable(event="unused")]
        public function get children():ListCollectionView {
            return _children;
        }

        [Bindable(event="unused")]
        public function get descriptions():ListCollectionView {
            return _descriptions;
        }

        [Bindable(event="unused")]
        public function get labels():ListCollectionView {
            return _labels;
        }

        [Bindable(event="unused")]
        public function get qualifiable():Boolean {
            return _qualifiable;
        }

        public function get codes():ListCollectionView {
            return null;
        }

        override public function readExternal(input:IDataInput):void {
            super.readExternal(input);
            _children = input.readObject() as ListCollectionView;
            _codeDefinitions = input.readObject() as ListCollectionView;
            _deprecatedAttribute = input.readObject() as String;
            _descriptions = input.readObject() as ListCollectionView;
            _labels = input.readObject() as ListCollectionView;
            _qualifiable = input.readObject() as Boolean;
            _sinceAttribute = input.readObject() as String;
        }

        override public function writeExternal(output:IDataOutput):void {
            super.writeExternal(output);
            output.writeObject(_children);
            output.writeObject(_codeDefinitions);
            output.writeObject(_deprecatedAttribute);
            output.writeObject(_descriptions);
            output.writeObject(_labels);
            output.writeObject(_qualifiable);
            output.writeObject(_sinceAttribute);
        }
    }
}