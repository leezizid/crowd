
import React from 'react';
import { Text, View } from 'react-native';
import BaseComponent from '../../commons/BaseComponent'

export default  class ContactView extends BaseComponent {

    constructor(props) {
        super(props);
    }

    renderContent() {
        return (
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
                <Text>Contact!</Text>
            </View>
        );
    }
}

