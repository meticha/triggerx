import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  icon: ReactNode;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Time-Triggered UI That Just Works',
    icon: '⏰',
    description: (
      <>
        Reliable alarms that launch your UI even if the app’s killed or in Doze. No hacks, just results.
      </>
    ),
  },
  {
    title: 'Fully Custom UI with Compose',
    icon: '⚙️',
    description: (
      <>
        Build any screen you want using Jetpack Compose. We’ll take care of showing it.
      </>
    ),
  },
  {
    title: 'Handles Permissions So You That Don’t Have To',
    icon: '🔐',
    description: (
      <>
        One-liner permission flow. No boilerplate, no confusion.
      </>
    ),
  },
];

function Feature({title, icon, description}: FeatureItem) {
  return (
    <div className="col col--4">
      <div className={clsx(styles.card, 'padding--md')}>
        <div className="text--center">
          <div className={styles.icon}>{icon}</div>
          <Heading as="h3">{title}</Heading>
          <p>{description}</p>
        </div>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={props.title} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
